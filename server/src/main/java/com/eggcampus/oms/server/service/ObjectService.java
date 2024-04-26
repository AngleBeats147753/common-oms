package com.eggcampus.oms.server.service;

import com.eggcampus.oms.server.pojo.dto.UploadTokenDTO;
import com.eggcampus.oms.server.pojo.qo.CheckStatusModificationQO;
import com.eggcampus.oms.server.pojo.qo.DeleteQO;
import com.eggcampus.oms.server.pojo.qo.UploadTokenGenerationQO;
import com.eggcampus.oms.server.pojo.qo.UsageQO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 黄磊
 */
public interface ObjectService {
    /**
     * OSS回调OMS时的路径
     */
    String OSS_CALLBACK_PATH = "/object/oss/callback";

    /**
     * 生成对象的上传凭证
     *
     * @param qo 生成上传凭证时所需的数据
     * @return 上传凭证
     */
    UploadTokenDTO generateUploadToken(UploadTokenGenerationQO qo);

    /**
     * 存储对象的使用信息，简称使用对象
     *
     * @param qo 存储对象使用信息时所需的数据
     */
    void use(UsageQO qo);

    /**
     * 批量使用资源对象
     *
     * @param usageQOList 存储对象使用信息所需的数据列表
     */
    void use(List<UsageQO> usageQOList);

    /**
     * 处理OSS的回调信息
     *
     * @param request 回调信息
     * @return 处理结果
     */
    String handleOssCallback(HttpServletRequest request);

    /**
     * 删除对象
     *
     * @param qo 删除对象时所需的数据
     */
    void delete(DeleteQO qo);


    /**
     * 批量删除存储对象
     *
     * @param deleteQoList 删除对象所需要的数据
     */
    void delete(List<DeleteQO> deleteQoList);

    /**
     * 修改对象的审核状态
     *
     * @param qo 修改对象审核状态时所需的数据
     */
    void modifyCheckStatus(CheckStatusModificationQO qo);
}
