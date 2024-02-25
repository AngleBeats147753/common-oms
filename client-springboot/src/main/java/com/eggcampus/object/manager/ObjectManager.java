package com.eggcampus.object.manager;

import com.eggcampus.object.enums.CheckStatus;
import com.eggcampus.object.pojo.UploadTokenDTO;
import com.eggcampus.util.spring.application.ApplicationDTO;

import java.util.List;

/**
 * 图像服务的客户端工具
 *
 * <p>
 * 图像服务器所需的applicationName、applicationProfile会通过{@link com.eggcampus.util.spring.application.ApplicationManager} 获取
 * </p>
 *
 * @author 黄磊
 */
public interface ObjectManager {
    /**
     * 创建上传凭证
     *
     * @param imageName   图片名称
     * @return 上传凭证
     */
    UploadTokenDTO createUploadToken(String imageName);

    /**
     * 使用图片
     *
     * @param objectUrl 资源对象URL
     */
    void useObject(String objectUrl);

    /**
     * 使用图片
     *
     * @param objectUrl  资源对象URL
     * @param needCheck 是否需要审核
     */
    void useObject(String objectUrl, Boolean needCheck);

    /**
     * 使用新资源并删除旧资源
     *
     * @param newServiceImages  新资源列表
     * @param oldServiceImages  旧资源列表
     */
    void useOrDeleteObject(List<String> newServiceImages, List<String> oldServiceImages);

    /**
     * 使用新资源并删除旧资源
     *
     * @param newServiceImages  新资源列表
     * @param oldServiceImages  旧资源列表
     */
    void useOrDeleteObject(List<String> newServiceImages, List<String> oldServiceImages, boolean needCheck);

    /**
     * 修改审核状态
     *
     * @param objectUrl  资源对象URL
     * @param checkStatusName 审核状态
     */
    void modifyCheckStatus(String objectUrl, String checkStatusName);

    /**
     * 删除图片
     *
     * @param objectUrl 图片URL
     */
    void deleteObject(String objectUrl);

    /**
     * 获取基础URL
     *
     * @return 基础URL
     */
    String getBaseUrl();

    void deleteObjectList(List<String> imageUrlList);

    void useObjectList(List<String> objectList, boolean needCheck);


}
