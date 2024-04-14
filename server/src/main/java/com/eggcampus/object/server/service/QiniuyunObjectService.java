package com.eggcampus.object.server.service;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import com.eggcampus.object.pojo.UploadTokenDTO;
import com.eggcampus.object.server.config.QiniuyunProperties;
import com.eggcampus.object.server.manager.ObjectManager;
import com.eggcampus.object.server.pojo.ApplicationDO;
import com.eggcampus.object.server.pojo.ObjectDO;
import com.eggcampus.object.server.pojo.ObjectDO.CheckStatus;
import com.eggcampus.object.server.pojo.ObjectDO.UsageStatus;
import com.eggcampus.object.server.pojo.qo.CheckStatusModificationQO;
import com.eggcampus.object.server.pojo.qo.DeleteQO;
import com.eggcampus.object.server.pojo.qo.UploadTokenGenerationQO;
import com.eggcampus.object.server.pojo.qo.UsageQO;
import com.eggcampus.util.exception.EggCampusException;
import com.eggcampus.util.result.AliErrorCode;
import com.eggcampus.util.spring.mybatisplus.exception.NotFoundException;
import com.qiniu.http.Headers;
import com.qiniu.storage.BucketManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 黄磊
 */
@Slf4j
public class QiniuyunObjectService implements ObjectService, InitializingBean {

    private final ApplicationService applicationService;
    private final ObjectManager objectManager;
    private final QiniuyunProperties properties;
    private BucketManager bucketManager;
    private Auth auth;
    private final String ossCallbackURL;

    public QiniuyunObjectService(ApplicationService applicationService,
                                 ObjectManager objectManager,
                                 QiniuyunProperties properties) {
        this.applicationService = applicationService;
        this.objectManager = objectManager;
        this.properties = properties;


        ossCallbackURL = UrlBuilder.of(properties.getOssCallbackDomain())
                .setCharset(null)
                .addPath(OSS_CALLBACK_PATH)
                .build();
    }

    @Override
    public void afterPropertiesSet() {
        this.auth = Auth.create(properties.getAccessKey(), properties.getSecretKey());
        this.bucketManager = new BucketManager(auth, properties.getBucketRegion());
    }

    @Override
    public UploadTokenDTO generateUploadToken(UploadTokenGenerationQO qo) {
        ApplicationDO application = applicationService.findApplication(qo.getApplication());
        String path = application.getPathPrefix() + qo.getObjectName();
        String url = getURL(path);
        objectManager.assertNonExistenceByURL(url);

        StringMap uploadPolicy = createImageUploadPolicy(url);
        saveImage(application.getId(), url);
        String token = auth.uploadToken(properties.getBucket(), path, properties.getUploadExpireSecond(), uploadPolicy);
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

    private StringMap createImageUploadPolicy(String url) {
        StringMap policy = new StringMap();
        policy.put("insertOnly", 1);
        policy.put("mimeLimit", "image/*");
        policy.put("fsizeLimit", properties.getFileSizeLimit().toBytes());
        policy.put("callbackUrl", ossCallbackURL);
        policy.put("callbackBodyType", "application/json");
        policy.put("callbackBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"%s\":\"%s\"}".formatted(OSS_CALLBACK_BODY_URL_KEY, url));
        return policy;
    }

    private void saveImage(Long applicationId, String url) {
        ObjectDO objectDO = new ObjectDO();
        objectDO.setUrl(url);
        objectDO.setUsageStatus(UsageStatus.GENERATED);
        objectDO.setGeneratedTime(LocalDateTime.now());
        objectDO.setType(ObjectDO.Type.IMAGE);
        objectDO.setCheckStatus(CheckStatus.UNCHECKED);
        objectDO.setApplicationId(applicationId);
        objectManager.save(objectDO);
    }

    @Override
    public void use(UsageQO qo) {
        ObjectDO objectDO = objectManager.findByURL(qo.getImageURL());
        if (objectDO == null) {
            throw new NotFoundException("资源对象不存在，url<%s>".formatted(qo.getImageURL()));
        }
        if (UsageStatus.USED.equals(objectDO.getUsageStatus())) {
            throw new EggCampusException(AliErrorCode.USER_ERROR_A0402, "资源对象已使用");
        }
        objectDO.setUsageStatus(UsageStatus.USED);
        objectDO.setUsedTime(LocalDateTime.now());
        objectDO.setCheckStatus(qo.getNeedCheck() ? CheckStatus.CHECKING : CheckStatus.NO_NEED_CHECK);
        objectManager.updateById(objectDO);
    }

    @Override
    public void modifyCheckStatus(CheckStatusModificationQO qo) {
        ObjectDO objectDO = objectManager.findByURL(qo.getImageURL());
        if (objectDO == null) {
            throw new NotFoundException("资源对象不存在，url<%s>".formatted(qo.getImageURL()));
        }
        if (CheckStatus.NO_NEED_CHECK.equals(objectDO.getCheckStatus())) {
            throw new EggCampusException(AliErrorCode.USER_ERROR_A0402, "不需要审核的资源对象不能修改审核状态");
        }
        objectDO.setCheckStatus(qo.getCheckStatus());
        objectManager.updateById(objectDO);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void useObjectList(List<UsageQO> usageQoList) {
        for (UsageQO usageQo : usageQoList) {
            use(usageQo);
        }
    }

    @Override
    public void delete(DeleteQO qo) {
        ObjectDO objectDO = objectManager.findByURL(qo.getObjectUrl());
        if (objectDO == null) {
            log.info("删除的资源对象不存在，url<%s>".formatted(qo.getObjectUrl()));
            return;
        }
        objectManager.removeById(objectDO.getId());
        try {
            String key = URLUtil.getPath(objectDO.getUrl()).substring(1);
            bucketManager.delete(properties.getBucket(), key);
        } catch (Exception e) {
            log.error("删除资源对象失败，url<%s>".formatted(qo.getObjectUrl()));
        }
    }

    @Override
    public void deleteObjectList(List<DeleteQO> deleteQoList) {
        for (DeleteQO deleteQo : deleteQoList) {
            delete(deleteQo);
        }
    }

    @Transactional
    @Override
    public String handleOssCallback(HttpServletRequest request) {
        try {
            byte[] bodyBytes = request.getInputStream().readAllBytes();
            String bodyStr = StrUtil.str(bodyBytes, "UTF-8");
            if (!validCallback(request, bodyBytes, bodyStr)) {
                return bodyStr;
            }

            String url = JSONUtil.parseObj(bodyStr).getStr(OSS_CALLBACK_BODY_URL_KEY);
            objectManager.assertUsageStatusByURL(url, UsageStatus.GENERATED);
            modifyUsageStatus(url, UsageStatus.UPLOADED);

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

    private void modifyUsageStatus(String url, UsageStatus status) {
        ObjectDO objectDO = objectManager.findByURL(url);
        objectDO.setUsageStatus(status);
        switch (status) {
            case UPLOADED:
                objectDO.setUploadedTime(LocalDateTime.now());
                break;
            case USED:
                objectDO.setUsedTime(LocalDateTime.now());
                break;
            case PRE_DELETED:
                objectDO.setPreDeletedTime(LocalDateTime.now());
            default:
                throw new IllegalArgumentException("不支持的使用状态<%s>".formatted(status));
        }
        objectManager.updateById(objectDO);
        log.debug("修改资源对象的使用状态成功，url<%s>，status<%s>".formatted(url, status));
    }
}
