package com.eggcampus.object.manager.impl;

import com.eggcampus.object.ObjectClient;
import com.eggcampus.object.enums.CheckStatus;
import com.eggcampus.object.manager.ObjectManager;
import com.eggcampus.object.pojo.UploadTokenDTO;
import com.eggcampus.util.spring.application.ApplicationDTO;
import lombok.RequiredArgsConstructor;

/**
 * @author huangshuaijie
 * @date 2024/2/15 17:03
 */
@RequiredArgsConstructor
public class ObjectManagerImpl implements ObjectManager {

    private final ObjectClient objectClient;


    @Override
    public UploadTokenDTO createUploadToken(ApplicationDTO application, String imageName) {
        return objectClient.generateUploadToken(application, imageName);
    }

    @Override
    public void useObject(String objectUrl) {
        useObject(objectUrl, false);
    }

    @Override
    public void useObject(String objectUrl, Boolean needCheck) {
        objectClient.useObject(objectUrl, needCheck);
    }

    @Override
    public void modifyCheckStatus(String objectUrl, String needCheck) {
        objectClient.modifyObjectCheckStatus(objectUrl, CheckStatus.createByName(needCheck));
    }

    @Override
    public void deleteObject(String objectUrl) {
        objectClient.deleteImage(objectUrl);
    }

    @Override
    public String getBaseUrl() {
        return objectClient.getBaseUrl();
    }
}
