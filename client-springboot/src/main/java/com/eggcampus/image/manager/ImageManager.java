package com.eggcampus.image.manager;

/**
 * 图像服务的客户端工具
 *
 * <p>
 * 图像服务器所需的applicationName、applicationProfile会通过{@link com.eggcampus.util.application.ApplicationManager} 获取
 * </p>
 *
 * @author 黄磊
 */
public interface ImageManager {
    /**
     * 创建上传凭证
     *
     * @param imageName 图片名称
     * @return 上传凭证
     */
    String createUploadToken(String imageName);

    /**
     * 使用图片
     *
     * @param ImageURL 图片URL
     */
    void useImage(String ImageURL);

    /**
     * 使用图片
     *
     * @param ImageURL  图片URL
     * @param needCheck 是否需要审核
     */
    void useImage(String ImageURL, String needCheck);

    /**
     * 修改审核状态
     *
     * @param ImageURL  图片URL
     * @param needCheck 是否需要审核
     */
    void modifyCheckStatus(String ImageURL, String needCheck);

    /**
     * 删除图片
     *
     * @param ImageURL 图片URL
     */
    void deleteImage(String ImageURL);

    /**
     * 获取基础URL
     *
     * @return 基础URL
     */
    String getBaseUrl();
}
