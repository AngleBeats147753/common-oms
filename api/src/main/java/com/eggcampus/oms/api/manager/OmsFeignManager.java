package com.eggcampus.oms.api.manager;

import com.eggcampus.oms.api.pojo.qo.DeleteObjectQo;
import com.eggcampus.oms.api.pojo.qo.UploadTokenGenerationQo;
import com.eggcampus.oms.api.pojo.qo.UseObjectQo;
import com.eggcampus.util.result.ReturnResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Set;

/**
 * @author 黄磊
 */
public interface OmsFeignManager {
    /**
     * 生成图像的上传凭证
     *
     * @param qo 生成上传凭证的查询对象
     * @return 上传凭证
     */
    @PostMapping("/image/upload-token")
    ReturnResult generateUploadToken(@RequestBody UploadTokenGenerationQo qo);

    /**
     * 使用资源
     *
     * @param qo 使用资源所需的数据
     * @return 使用结果
     */
    @PutMapping("/object/use")
    ReturnResult use(@RequestBody UseObjectQo qo);

    /**
     * 删除资源
     *
     * @param qo 删除资源所需的数据
     * @return 删除结果
     */
    @DeleteMapping("/object/delete")
    ReturnResult delete(@RequestBody DeleteObjectQo qo);
}
