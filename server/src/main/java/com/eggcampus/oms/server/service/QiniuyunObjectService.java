package com.eggcampus.oms.server.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import com.eggcampus.oms.api.pojo.ApplicationDO;
import com.eggcampus.oms.api.pojo.ObjectDO;
import com.eggcampus.oms.api.pojo.ObjectDO.UsageStatus;
import com.eggcampus.oms.api.pojo.dto.UploadTokenDTO;
import com.eggcampus.oms.api.pojo.qo.DeletionQuery;
import com.eggcampus.oms.api.pojo.qo.UploadTokenGenerationQuery;
import com.eggcampus.oms.server.config.QiniuyunProperties;
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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 黄磊
 */
@Slf4j
public class QiniuyunObjectService implements ObjectService {

    /**
     * 七牛云回调的请求体中url的key
     */
    String CALLBACK_BODY_URL_KEY = "url";

    private final ApplicationService applicationService;
    private final ObjectManager objectManager;
    private final QiniuyunProperties properties;
    private final String ossCallbackURL;
    private final BucketManager bucketManager;
    private final String bucketName;
    private final Auth auth;

    public QiniuyunObjectService(ApplicationService applicationService,
                                 ObjectManager objectManager,
                                 QiniuyunProperties properties) {
        this.applicationService = applicationService;
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
    public UploadTokenDTO generateImageUploadToken(UploadTokenGenerationQuery query) {
        ApplicationDO application = applicationService.findApplication(query.getApplication());
        String path = application.getPathPrefix() + query.getImageName();
        String url = getURL(path);
        objectManager.assertNonExistenceByURL(url);

        StringMap uploadPolicy = createImageUploadPolicy(path, url);
        saveObject(application.getId(), url);
        String token = auth.uploadToken(properties.getBucket(), path, properties.getUploadExpireSecond(), uploadPolicy);
        log.info("生成图像上传凭证，url<%s>".formatted(url));
        return new UploadTokenDTO(path, token, url);
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
        ObjectDO objectDO = new ObjectDO();
        objectDO.setUrl(url);
        objectDO.setUsageStatus(UsageStatus.GENERATED);
        objectDO.setGeneratedTime(LocalDateTime.now());
        objectDO.setType(ObjectDO.Type.IMAGE);
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
            ObjectDO objectDO = objectManager.findByURL(url);
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
        List<ObjectDO> objectDOS = objectManager.listByURL(urls);
        assertResourceExistence(urls, objectDOS.stream().map(ObjectDO::getUrl).toList());

        for (ObjectDO objectDO : objectDOS) {
            objectManager.assertUsageStatus(objectDO, UsageStatus.UPLOADED);
            objectDO.setUsageStatus(UsageStatus.USED);
            objectDO.setUsedTime(LocalDateTime.now());
        }
        objectManager.updateBatchById(objectDOS);
        log.info("使用资源成功，urls<%s>".formatted(urls));
    }

    @Override
    @GlobalTransactional(name = "删除资源-临时")
    public void deleteTemporarily(Set<DeletionQuery> queries) {
        List<String> urls = queries.stream().map(DeletionQuery::getObjectUrl).toList();
        List<ObjectDO> objectDOS = objectManager.listByURL(urls);

        // 由于有资源可能不存在，无法使用索引来找到删除原因，所以转换为map根据url找删除原因
        Map<String, String> reasonMap = queries.stream().collect(Collectors.toMap(DeletionQuery::getObjectUrl, DeletionQuery::getDeletionReason));
        for (ObjectDO objectDO : objectDOS) {
            if (UsageStatus.MARKED_DELETION.equals(objectDO.getUsageStatus())) {
                continue;
            }
            objectManager.assertUsageStatus(objectDO, UsageStatus.USED);
            objectDO.setUsageStatus(UsageStatus.MARKED_DELETION);
            objectDO.setMarkedDeletionTime(LocalDateTime.now());
            objectDO.setDeletionReason(reasonMap.get(objectDO.getUrl()));
        }
        objectManager.updateBatchById(objectDOS);
        log.info("临时删除资源成功，urls<%s>".formatted(urls));
    }

    @Override
    @GlobalTransactional(name = "删除资源-永久")
    public void deletePermanently(Set<String> queries) {
        List<String> urls = queries.stream().toList();
        for (String url : urls) {
            String key = URLUtil.getPath(url).substring(1);
            ObjectDO object = objectManager.getByURL(url);
            if (object == null) {
                continue;
            }
            if (!UsageStatus.MARKED_DELETION.equals(object.getUsageStatus())) {
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
