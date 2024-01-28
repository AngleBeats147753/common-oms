package com.eggcampus.image.server.service;

import com.eggcampus.image.server.pojo.qo.CheckStatusModificationQO;
import com.eggcampus.image.server.pojo.qo.DeleteQO;
import com.eggcampus.image.server.pojo.qo.UploadTokenGenerationQO;
import com.eggcampus.image.server.pojo.qo.UsageQO;
import com.eggcampus.util.result.ReturnResult;

/**
 * @author 黄磊
 */
public interface ImageService {
    /**
     * 生成对象的上传凭证
     *
     * @param qo 生成上传凭证时所需的数据
     * @return 上传凭证
     */
    ReturnResult generateUploadToken(UploadTokenGenerationQO qo);

    /**
     * 存储对象的使用信息，简称使用对象
     *
     * @param qo 存储对象使用信息时所需的数据
     */
    void use(UsageQO qo);

    /**
     * 修改对象的审核状态
     *
     * @param qo 修改对象审核状态时所需的数据
     */
    void modifyCheckStatus(CheckStatusModificationQO qo);

    /**
     * 删除对象
     *
     * @param qo 删除对象时所需的数据
     */
    void delete(DeleteQO qo);
}
