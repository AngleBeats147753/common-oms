package com.eggcampus.oms.client.springboot;

import com.eggcampus.oms.api.pojo.dto.UploadTokenDto;

import java.util.Collection;

/**
 * @author 黄磊
 */
public interface OmsManager {
    /**
     * 生成图像的上传凭证
     *
     * @param imageName 图像名称
     * @return 上传凭证
     */
    UploadTokenDto generateImageUploadToken(String imageName);

    /**
     * 使用资源
     *
     * @param urls 资源urls
     */
    void use(String urls);

    /**
     * 使用资源
     *
     * @param urls 资源urls
     */
    void use(Collection<String> urls);

    /**
     * 删除资源
     *
     * @param urls 资源的URLs
     */
    void delete(String urls);

    /**
     * 删除资源
     *
     * @param urls 资源的URL
     */
    void delete(Collection<String> urls);

    /**
     * 修改资源
     * <p>
     * 对比新旧资源。如果新资源中有旧资源中没有的，就添加；如果旧资源中有新资源中没有的，就删除。其余保持不变
     * </p>
     *
     * @param oldUrls 旧资源的URL，多个资源之间用英文逗号分隔
     * @param newUrls 新资源的URL，多个资源之间用英文逗号分隔
     */
    void change(String oldUrls, String newUrls);

    /**
     * 修改资源
     * <p>
     * 对比新旧资源。如果新资源中有旧资源中没有的，就添加；如果旧资源中有新资源中没有的，就删除。其余保持不变
     * </p>
     *
     * @param oldUrls 旧资源的URL
     * @param newUrls 新资源的URL
     */
    void change(Collection<String> oldUrls, Collection<String> newUrls);
}
