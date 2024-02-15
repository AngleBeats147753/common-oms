package com.eggcampus.object.manager;

import com.eggcampus.object.pojo.UploadTokenDTO;
import com.eggcampus.util.spring.application.ApplicationDTO;

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
     * @param application 应用名称
     * @return 上传凭证
     */
    UploadTokenDTO createUploadToken(ApplicationDTO application, String imageName);

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
}