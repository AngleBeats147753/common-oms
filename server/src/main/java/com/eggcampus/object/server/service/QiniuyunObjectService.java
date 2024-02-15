package com.eggcampus.object.server.service;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.URLUtil;
import com.eggcampus.object.enums.CheckStatus;
import com.eggcampus.object.enums.ObjectTypeEnum;
import com.eggcampus.object.pojo.UploadTokenDTO;
import com.eggcampus.object.server.config.QiniuyunProperties;
import com.eggcampus.object.server.manager.ApplicationManager;
import com.eggcampus.object.server.manager.ObjectManager;
import com.eggcampus.object.server.pojo.ApplicationDO;
import com.eggcampus.object.server.pojo.ObjectDO;
import com.eggcampus.object.server.pojo.qo.CheckStatusModificationQO;
import com.eggcampus.object.server.pojo.qo.DeleteQO;
import com.eggcampus.object.server.pojo.qo.UploadTokenGenerationQO;
import com.eggcampus.object.server.pojo.qo.UsageQO;
import com.eggcampus.util.exception.EggCampusException;
import com.eggcampus.util.result.AliErrorCode;
import com.eggcampus.util.result.ReturnResult;
import com.eggcampus.util.spring.application.ApplicationDTO;
import com.eggcampus.util.spring.mybatisplus.exception.NotFoundException;
import com.qiniu.storage.BucketManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 黄磊
 */
@Slf4j
public class QiniuyunObjectService implements ObjectService, InitializingBean {
    private final ApplicationManager applicationManager;
    private final ObjectManager objectManager;
    private final QiniuyunProperties properties;
    private BucketManager bucketManager;
    private Auth auth;

    public QiniuyunObjectService(ApplicationManager applicationManager,
                                 ObjectManager objectManager,
                                 QiniuyunProperties properties) {
        this.applicationManager = applicationManager;
        this.objectManager = objectManager;
        this.properties = properties;
    }

    @Override
    public void afterPropertiesSet() {
        this.auth = Auth.create(properties.getAccessKey(), properties.getSecretKey());
        this.bucketManager = new BucketManager(auth, properties.getBucketRegion());
    }

    @Override
    public ReturnResult generateUploadToken(UploadTokenGenerationQO qo) {
        ApplicationDO application = findApplication(qo.getApplication());
        String path = application.getPathPrefix() + qo.getImageName();
        String url = getURL(path);
        assertURLNotExists(url);

        StringMap uploadPolicy = createUploadPolicy();
        saveImageDO(application.getId(), url);
        String token = auth.uploadToken(properties.getBucket(), path, properties.getUploadExpireSecond(), uploadPolicy);
        return ReturnResult.getSuccessReturn(new UploadTokenDTO(path, token, url));
    }

    private void assertURLNotExists(String url) {
        if (objectManager.getByURL(url) != null) {
            throw new EggCampusException(AliErrorCode.USER_ERROR_A0402, "资源对象已存在");
        }
    }

    private String getURL(String path) {
        return UrlBuilder.of()
                .setCharset(null)
                .setScheme("https")
                .setHost(properties.getDomainOfBucket())
                .addPath(path)
                .build();
    }

    private ApplicationDO findApplication(ApplicationDTO dto) {
        return applicationManager.findByNameAndProfile(dto.getName(), dto.getProfile());
    }

    private StringMap createUploadPolicy() {
        StringMap map = new StringMap();
        map.put("insertOnly", 1);
        map.put("mimeLimit", "image/*");
        map.put("fsizeLimit", properties.getFileSizeLimit().toBytes());
        return map;
    }

    private void saveImageDO(Long applicationId, String url) {
        ObjectDO objectDO = new ObjectDO();
        objectDO.setUrl(url);
        objectDO.setUsed(false);
        objectDO.setObjectTypeEnum(ObjectTypeEnum.IMAGE);
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
        if (objectDO.getUsed()) {
            throw new EggCampusException(AliErrorCode.USER_ERROR_A0402, "资源对象已使用");
        }
        objectDO.setUsed(true);
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
}
