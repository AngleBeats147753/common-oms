package com.eggcampus.oms.server.service;

import com.eggcampus.oms.api.pojo.dto.UploadTokenDTO;
import com.eggcampus.oms.api.pojo.qo.UploadTokenGenerationQuery;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * @author 黄磊
 */
public interface ObjectService {
    /**
     * OSS回调OMS时的路径
     */
    String OSS_CALLBACK_PATH = "/object/oss/callback";

    /**
     * 生成图像的上传凭证
     *
     * @param query 生成上传凭证时所需的数据
     * @return 上传凭证
     */
    UploadTokenDTO generateImageUploadToken(UploadTokenGenerationQuery query);

    /**
     * 处理OSS的回调信息
     *
     * @param request 回调信息
     * @return 处理结果
     */
    String handleOssCallback(HttpServletRequest request);

    /**
     * 使用资源对象
     *
     * @param queries 资源对象使用信息所需的数据列表
     */
    void use(Set<String> queries);

    /**
     * 删除资源对象
     * <p>
     * 注意1：为了防止审核系统删除资源对象，但是业务系统不知道对象已被删除了的情况。当资源对象的使用状态为待删除或资源对象不存在时，该接口也不会返回错误
     * 注意2：该方法不会立即删除资源对象
     *
     * @param queries 资源对象url列表
     */
    void delete(Set<String> queries);


    /**
     * 立即删除资源
     *
     * @param urls 资源对象url列表
     */
    void deleteImmediately(Set<String> urls);
}
