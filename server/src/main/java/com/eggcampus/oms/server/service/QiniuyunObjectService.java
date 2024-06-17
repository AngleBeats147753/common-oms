package com.eggcampus.oms.server.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import com.eggcampus.oms.api.constant.DeletionReason;
import com.eggcampus.oms.api.pojo.ApplicationDo;
import com.eggcampus.oms.api.pojo.ObjectDo;
import com.eggcampus.oms.api.pojo.ObjectDo.UsageStatus;
import com.eggcampus.oms.api.pojo.dto.UploadTokenDto;
import com.eggcampus.oms.api.pojo.qo.UploadTokenGenerationQo;
import com.eggcampus.oms.server.config.QiniuyunProperties;
import com.eggcampus.oms.server.manager.ApplicationManager;
import com.eggcampus.oms.server.manager.ObjectManager;
import com.eggcampus.util.exception.result.ServiceException;
import com.eggcampus.util.result.AliErrorCode;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Headers;
import com.qiniu.storage.BucketManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * @author 黄磊
 */
@Slf4j
public class QiniuyunObjectService implements ObjectService {

    /**
     * 七牛云回调的请求体中url的key
     */
    String CALLBACK_BODY_URL_KEY = "url";

    private final ApplicationManager applicationManager;
    private final ObjectManager objectManager;
    private final QiniuyunProperties properties;
    private final String ossCallbackURL;
    private final BucketManager bucketManager;
    private final String bucketName;
    private final Auth auth;

    public QiniuyunObjectService(ApplicationManager applicationManager,
                                 ObjectManager objectManager,
                                 QiniuyunProperties properties) {
        this.applicationManager = applicationManager;
        this.objectManager = objectManager;
        this.properties = properties;
        this.auth = Auth.create(properties.getAccessKey(), properties.getSecretKey());
        this.bucketManager = new BucketManager(auth, properties.getBucketRegion());
        this.bucketName = properties.getBucket();

        ossCallbackURL = UrlBuilder.of(properties.getOssCallbackDomain())
                .setCharset(null)
                .addPath(OSS_CALLBACK_PATH)
                .build();
    }

    @Override
    @GlobalTransactional(name = "生成图像的上传凭证")
    public UploadTokenDto generateImageUploadToken(UploadTokenGenerationQo qo) {
        ApplicationDo application = applicationManager.findById(qo.getApplicationId());
        String path = application.getPathPrefix() + qo.getImageName();
        String url = getURL(path);
        objectManager.assertNonExistenceByURL(url);

        StringMap uploadPolicy = createImageUploadPolicy(path, url);
        saveObject(application.getId(), url);
        String token = auth.uploadToken(properties.getBucket(), path, properties.getUploadExpireSecond(), uploadPolicy);
        log.info("生成图像上传凭证，url<%s>".formatted(url));
        return new UploadTokenDto(path, token, url);
    }

    private String getURL(String path) {
        return UrlBuilder.of()
                .setCharset(null)
                .setScheme("https")
                .setHost(properties.getDomainOfBucket())
                .addPath(path)
                .build();
    }

    private StringMap createImageUploadPolicy(String key, String url) {
        StringMap policy = new StringMap();
        policy.put("insertOnly", 1);
        policy.put("mimeLimit", "image/*");
        policy.put("fsizeLimit", properties.getFileSizeLimit().toBytes());
        policy.put("forceSaveKey", true);
        policy.put("saveKey", key);
        policy.put("callbackUrl", ossCallbackURL);
        policy.put("callbackBodyType", "application/json");
        policy.put("callbackBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"%s\":\"%s\"}".formatted(CALLBACK_BODY_URL_KEY, url));
        return policy;
    }

    private void saveObject(Long applicationId, String url) {
        ObjectDo objectDO = new ObjectDo();
        objectDO.setUrl(url);
        objectDO.setUsageStatus(UsageStatus.GENERATED);
        objectDO.setUsageNum(0);
        objectDO.setGeneratedTime(LocalDateTime.now());
        objectDO.setType(ObjectDo.Type.IMAGE);
        objectDO.setApplicationId(applicationId);
        objectManager.save(objectDO);
    }

    @Override
    @Transactional
    public String handleOssCallback(HttpServletRequest request) {
        try {
            byte[] bodyBytes = request.getInputStream().readAllBytes();
            String bodyStr = StrUtil.str(bodyBytes, "UTF-8");
            if (!validCallback(request, bodyBytes, bodyStr)) {
                return bodyStr;
            }

            String url = JSONUtil.parseObj(bodyStr).getStr(CALLBACK_BODY_URL_KEY);
            ObjectDo objectDO = objectManager.findByURL(url);
            objectDO.setUsageStatus(UsageStatus.UPLOADED);
            objectDO.setUploadedTime(LocalDateTime.now());
            objectManager.updateById(objectDO);
            log.info("处理上传回调成功，资源已成功被使用，url<%s>".formatted(url));

            return bodyStr;
        } catch (Exception e) {
            log.error("七牛云回调失败", e);
            return "";
        }
    }

    private boolean validCallback(HttpServletRequest request, byte[] bodyBytes, String bodyStr) {
        String authorization = request.getHeader("Authorization");

        Auth.Request qiniuRequest = new Auth.Request(request.getRequestURL().toString(),
                request.getMethod(),
                Headers.of(ServletUtil.getHeaderMap(request)),
                bodyBytes);
        if (auth.isValidCallback(authorization, qiniuRequest)) {
            return true;
        }
        log.error("七牛云回调验证失败，Authorization<%s>，body<%s>".formatted(authorization, bodyStr));
        return false;
    }

    @Override
    @GlobalTransactional(name = "使用资源")
    public void use(Set<String> queries) {
        List<String> urls = queries.stream().toList();
        List<ObjectDo> objectDos = objectManager.listByURL(urls);
        assertResourceExistence(urls, objectDos.stream().map(ObjectDo::getUrl).toList());

        for (ObjectDo objectDO : objectDos) {
            if (UsageStatus.UPLOADED.equals(objectDO.getUsageStatus())) {
                objectDO.setUsageStatus(UsageStatus.USED);
                objectDO.setUsageNum(objectDO.getUsageNum() + 1);
                objectDO.setUsedTime(LocalDateTime.now());
            } else if (UsageStatus.USED.equals(objectDO.getUsageStatus())) {
                objectDO.setUsageNum(objectDO.getUsageNum() + 1);
            } else if (UsageStatus.DELETION_MARKED.equals(objectDO.getUsageStatus())) {
                objectDO.setUsageStatus(UsageStatus.USED);
                objectDO.setUsageNum(objectDO.getUsageNum() + 1);
                objectDO.setUsedTime(LocalDateTime.now());
                objectDO.setDeletionReason(null);
                objectDO.setMarkedDeletionTime(null);
            } else if (UsageStatus.DELETED.equals(objectDO.getUsageStatus())) {
                throw new ServiceException(AliErrorCode.USER_ERROR_A0402, "资源已被删除，url<%s>，删除原因<%s>".formatted(objectDO.getUrl(), objectDO.getDeletionReason()));
            } else {
                throw new ServiceException(AliErrorCode.USER_ERROR_A0402, "资源状态异常，url<%s>，期望状态<UPLOADED、USED、DELETION_MARKED>，当前状态<%s>".formatted(objectDO.getUrl(), objectDO.getUsageStatus()));
            }
        }
        objectManager.updateBatchById(objectDos);
        log.info("使用资源成功，urls<%s>".formatted(urls));
    }

    @Override
    @GlobalTransactional(name = "删除资源")
    public void delete(Set<String> urls) {
        List<ObjectDo> objectDos = objectManager.listByURL(urls);

        for (ObjectDo objectDO : objectDos) {
            if (UsageStatus.USED.equals(objectDO.getUsageStatus())) {
                objectDO.setUsageNum(objectDO.getUsageNum() - 1);
                log.info("减少资源使用数，url<%s>，剩余使用数<%d>".formatted(objectDO.getUrl(), objectDO.getUsageNum()));
                if (objectDO.getUsageNum() == 0) {
                    objectDO.setUsageStatus(UsageStatus.DELETION_MARKED);
                    objectDO.setDeletionReason(DeletionReason.BUSINESS_DELETION);
                    objectDO.setMarkedDeletionTime(LocalDateTime.now());
                    log.info("标记资源删除，url<%s>".formatted(objectDO.getUrl()));
                }
            } else if (UsageStatus.DELETION_MARKED.equals(objectDO.getUsageStatus()) ||
                    UsageStatus.DELETED.equals(objectDO.getUsageStatus())) {
                log.warn("正在删除已删除资源，url<%s>，删除原因<%s>".formatted(objectDO.getUrl(), objectDO.getDeletionReason()));
            } else {
                throw new ServiceException(AliErrorCode.USER_ERROR_A0402, "资源状态异常，url<%s>，期望状态<UPLOADED、USED、DELETION_MARKED>，当前状态<%s>".formatted(objectDO.getUrl(), objectDO.getUsageStatus()));
            }
        }
        objectManager.updateBatchById(objectDos);
        log.info("临时删除资源成功，urls<%s>".formatted(urls));
    }

    @Override
    @GlobalTransactional(name = "立即删除资源")
    public void deleteImmediately(Set<String> queries) {
        List<String> urls = queries.stream().toList();
        for (String url : urls) {
            String key = URLUtil.getPath(url).substring(1);
            ObjectDo object = objectManager.getByURL(url);
            if (object == null) {
                continue;
            }
            if (!UsageStatus.DELETION_MARKED.equals(object.getUsageStatus())) {
                throw new ServiceException(AliErrorCode.USER_ERROR_A0402, "资源的使用状态不是待删除，url<%s>".formatted(url));
            }
            try {
                bucketManager.delete(bucketName, key);
                log.info("从七牛云中删除资源成功，url<%s>".formatted(url));
            } catch (QiniuException ex) {
                log.warn("从七牛云中删除资源失败，错误编码<%s>，错误信息<%s>".formatted(ex.code(), ex.response.toString()));
            }
            objectManager.removeById(object);
            log.info("永久删除资源成功，url<%s>".formatted(url));
        }
    }


    private static <T> void assertResourceExistence(List<T> srcResource, List<T> targetResource) {
        List<T> result = CollUtil.subtractToList(srcResource, targetResource);
        if (!result.isEmpty()) {
            throw new ServiceException(AliErrorCode.USER_ERROR_A0402, "有资源未找到，url为<%s>".formatted(result));
        }
    }
}
