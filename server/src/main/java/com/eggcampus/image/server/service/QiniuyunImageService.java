package com.eggcampus.image.server.service;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.URLUtil;
import com.eggcampus.image.enums.CheckStatus;
import com.eggcampus.image.pojo.UploadTokenDTO;
import com.eggcampus.image.server.config.QiniuyunProperties;
import com.eggcampus.image.server.manager.ApplicationManager;
import com.eggcampus.image.server.manager.ImageManager;
import com.eggcampus.image.server.pojo.ApplicationDO;
import com.eggcampus.image.server.pojo.ImageDO;
import com.eggcampus.image.server.pojo.qo.CheckStatusModificationQO;
import com.eggcampus.image.server.pojo.qo.DeleteQO;
import com.eggcampus.image.server.pojo.qo.UploadTokenGenerationQO;
import com.eggcampus.image.server.pojo.qo.UsageQO;
import com.eggcampus.util.exception.EggCampusException;
import com.eggcampus.util.result.AliErrorCode;
import com.eggcampus.util.result.ReturnResult;
import com.eggcampus.util.spring.application.ApplicationDTO;
import com.qiniu.storage.BucketManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @author 黄磊
 */
public class QiniuyunImageService implements ImageService, InitializingBean {
    private final ApplicationManager applicationManager;
    private final ImageManager imageManager;
    private final QiniuyunProperties properties;
    private BucketManager bucketManager;
    private Auth auth;

    public QiniuyunImageService(ApplicationManager applicationManager,
                                ImageManager imageManager,
                                QiniuyunProperties properties) {
        this.applicationManager = applicationManager;
        this.imageManager = imageManager;
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
        if (imageManager.getByURL(url) != null) {
            throw new EggCampusException(AliErrorCode.USER_ERROR_A0402, "图像已存在");
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
        ImageDO imageDO = new ImageDO();
        imageDO.setUrl(url);
        imageDO.setUsed(false);
        imageDO.setCheckStatus(CheckStatus.UNCHECKED);
        imageDO.setApplicationId(applicationId);
        imageManager.save(imageDO);
    }

    @Override
    public void use(UsageQO qo) {
        ImageDO imageDO = imageManager.findByURL(qo.getImageURL());
        if (imageDO.getUsed()) {
            throw new EggCampusException(AliErrorCode.USER_ERROR_A0402, "图像已使用");
        }
        imageDO.setUsed(true);
        imageDO.setUsedTime(LocalDateTime.now());
        imageDO.setCheckStatus(qo.getNeedCheck() ? CheckStatus.CHECKING : CheckStatus.NO_NEED_CHECK);
        imageManager.updateById(imageDO);
    }

    @Override
    public void modifyCheckStatus(CheckStatusModificationQO qo) {
        ImageDO imageDO = imageManager.findByURL(qo.getImageURL());
        if (CheckStatus.NO_NEED_CHECK.equals(imageDO.getCheckStatus())) {
            throw new EggCampusException(AliErrorCode.USER_ERROR_A0402, "不需要审核的图像不能修改审核状态");
        }
        imageDO.setCheckStatus(qo.getCheckStatus());
        imageManager.updateById(imageDO);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(DeleteQO qo) {
        ImageDO imageDO = imageManager.findByURL(qo.getImageURL());
        imageManager.removeById(imageDO.getId());
        try {
            String key = URLUtil.getPath(imageDO.getUrl()).substring(1);
            bucketManager.delete(properties.getBucket(), key);
        } catch (Exception e) {
            throw new EggCampusException(AliErrorCode.SERVICE_ERROR_C0001, "删除图像失败", e);
        }
    }
}
