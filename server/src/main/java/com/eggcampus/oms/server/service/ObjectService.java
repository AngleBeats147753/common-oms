package com.eggcampus.oms.server.service;

import com.eggcampus.oms.api.pojo.dto.UploadTokenDto;
import com.eggcampus.oms.api.pojo.qo.DeleteObjectQo;
import com.eggcampus.oms.api.pojo.qo.UploadTokenGenerationQo;
import com.eggcampus.oms.api.pojo.qo.UseObjectQo;

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
    UploadTokenDto generateImageUploadToken(UploadTokenGenerationQo query);

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
     * @param qo 使用资源对象所需要的数据
     */
    void use(UseObjectQo qo);

    /**
     * 删除资源对象
     * <p>
     * 注意1：为了防止审核系统删除资源对象，但是业务系统不知道对象已被删除等情况。当资源对象的使用状态为待删除或资源对象不存在时，该接口也不会返回错误
     * 注意2：该方法不会立即删除资源对象
     *
     * @param qo 删除资源对象所需要的数据
     */
    void delete(DeleteObjectQo qo);


    /**
     * 立即删除资源
     *
     * @param urls 资源对象url列表
     */
    void deleteImmediately(Set<String> urls);
}
