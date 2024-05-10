package com.eggcampus.oms.client.springboot;

import com.eggcampus.oms.api.pojo.dto.UploadTokenDTO;
import com.eggcampus.oms.api.pojo.qo.DeletionQuery;

import java.util.List;

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
    UploadTokenDTO generateUploadToken(String imageName);

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
    void use(List<String> urls);

    /**
     * 删除资源
     *
     * @param urls 资源的URLs
     */
    void delete(String urls);

    /**
     * 删除资源
     *
     * @param query 删除信息
     */
    void WithReason(DeletionQuery query);

    /**
     * 删除资源
     *
     * @param urls 资源的URL
     */
    void delete(List<String> urls);

    /**
     * 删除资源
     *
     * @param queries 删除信息
     */
    void deleteWithReason(List<DeletionQuery> queries);

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
    void change(List<String> oldUrls, List<String> newUrls);
}
