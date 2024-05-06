package com.eggcampus.oms.api.manager;

import com.eggcampus.oms.api.pojo.qo.UploadTokenGenerationQuery;
import com.eggcampus.oms.api.pojo.qo.UsageQuery;
import com.eggcampus.util.result.ReturnResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

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
    ReturnResult generateUploadToken(@Validated @RequestBody UploadTokenGenerationQuery qo);

    /**
     * 使用资源
     *
     * @param queries 使用资源的查询对象
     * @return 使用结果
     */
    @PutMapping("/object/use")
    ReturnResult use(@Validated @RequestBody List<UsageQuery> queries);

    /**
     * 删除资源
     *
     * @param urls 资源的URL
     * @return 删除结果
     */
    @DeleteMapping("/object/delete")
    ReturnResult delete(@Validated @RequestBody List<String> urls);
}
