package com.eggcampus.oms.server.service;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import com.eggcampus.oms.server.config.QiniuyunProperties;
import com.eggcampus.oms.server.manager.ObjectManager;
import com.eggcampus.oms.server.pojo.ApplicationDO;
import com.eggcampus.oms.server.pojo.ObjectDO;
import com.eggcampus.oms.server.pojo.dto.UploadTokenDTO;
import com.eggcampus.oms.server.pojo.qo.CheckStatusModificationQO;
import com.eggcampus.oms.server.pojo.qo.DeleteQO;
import com.eggcampus.oms.server.pojo.qo.UploadTokenGenerationQO;
import com.eggcampus.oms.server.pojo.qo.UsageQO;
import com.eggcampus.util.exception.result.ServiceException;
import com.eggcampus.util.result.AliErrorCode;
import com.qiniu.http.Headers;
import com.qiniu.storage.BucketManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

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
    private final Auth auth;

    public QiniuyunObjectService(ApplicationService applicationService,
                                 ObjectManager objectManager,
                                 QiniuyunProperties properties) {
        this.applicationService = applicationService;
        this.objectManager = objectManager;
        this.properties = properties;
        this.auth = Auth.create(properties.getAccessKey(), properties.getSecretKey());
        this.bucketManager = new BucketManager(auth, properties.getBucketRegion());

        ossCallbackURL = UrlBuilder.of(properties.getOssCallbackDomain())
                .setCharset(null)
                .addPath(OSS_CALLBACK_PATH)
                .build();
    }

    @Override
    public UploadTokenDTO generateUploadToken(UploadTokenGenerationQO qo) {
        ApplicationDO application = applicationService.findApplication(qo.getApplication());
        String path = application.getPathPrefix() + qo.getImageName();
        String url = getURL(path);
        objectManager.assertNonExistenceByURL(url);

        StringMap uploadPolicy = createImageUploadPolicy(path, url);
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

    private void saveImage(Long applicationId, String url) {
        ObjectDO objectDO = new ObjectDO();
        objectDO.setUrl(url);
        objectDO.setUsageStatus(ObjectDO.UsageStatus.GENERATED);
        objectDO.setGeneratedTime(LocalDateTime.now());
        objectDO.setType(ObjectDO.Type.IMAGE);
        objectDO.setCheckStatus(ObjectDO.CheckStatus.UNCHECKED);
        objectDO.setApplicationId(applicationId);
        objectManager.save(objectDO);
    }

    @Override
    @Transactional
    public void use(UsageQO qo) {
        ObjectDO objectDO = objectManager.findByURL(qo.getImageURL());
        modifyUsageStatus(objectDO, ObjectDO.UsageStatus.USED, qo.getNeedCheck());
    }

    @Override
    @Transactional
    public void modifyCheckStatus(CheckStatusModificationQO qo) {
        ObjectDO objectDO = objectManager.findByURL(qo.getImageURL());
        if (ObjectDO.CheckStatus.NO_NEED_CHECK.equals(objectDO.getCheckStatus())) {
            throw new ServiceException(AliErrorCode.USER_ERROR_A0402, "不需要审核的资源对象不能修改审核状态");
        }
        objectDO.setCheckStatus(qo.getCheckStatus());
        objectManager.updateById(objectDO);
    }

    @Override
    @Transactional
    public void use(List<UsageQO> usageQoList) {
        for (UsageQO usageQo : usageQoList) {
            use(usageQo);
        }
    }

    @Override
    @Transactional
    public void delete(DeleteQO qo) {
        ObjectDO objectDO = objectManager.findByURL(qo.getObjectUrl());
        modifyUsageStatus(objectDO, ObjectDO.UsageStatus.PRE_DELETED);
    }

    @Override
    @Transactional
    public void delete(List<DeleteQO> deleteQoList) {
        for (DeleteQO deleteQo : deleteQoList) {
            delete(deleteQo);
        }
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
            modifyUsageStatus(url, ObjectDO.UsageStatus.UPLOADED);

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

    private void modifyUsageStatus(String url, ObjectDO.UsageStatus status) {
        ObjectDO objectDO = objectManager.findByURL(url);
        modifyUsageStatus(objectDO, status, null);
    }

    private void modifyUsageStatus(ObjectDO objectDO, ObjectDO.UsageStatus status) {
        modifyUsageStatus(objectDO, status, null);
    }

    private void modifyUsageStatus(ObjectDO objectDO, ObjectDO.UsageStatus status, Boolean needCheck) {
        switch (status) {
            case UPLOADED:
                objectManager.assertUsageStatus(objectDO, ObjectDO.UsageStatus.GENERATED);
                objectDO.setUploadedTime(LocalDateTime.now());
                break;
            case USED:
                objectManager.assertUsageStatus(objectDO, ObjectDO.UsageStatus.UPLOADED);
                objectDO.setUsedTime(LocalDateTime.now());
                objectDO.setCheckStatus(needCheck ? ObjectDO.CheckStatus.CHECKING : ObjectDO.CheckStatus.NO_NEED_CHECK);
                break;
            case PRE_DELETED:
                objectManager.assertUsageStatus(objectDO, ObjectDO.UsageStatus.USED);
                objectDO.setPreDeletedTime(LocalDateTime.now());
                break;
            default:
                throw new IllegalArgumentException("不支持的使用状态<%s>".formatted(status));
        }
        objectDO.setUsageStatus(status);
        objectManager.updateById(objectDO);
        log.debug("修改资源对象的使用状态成功，id<%s>，status<%s>".formatted(objectDO.getId(), status));
    }
}