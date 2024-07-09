package com.eggcampus.oms.server.controller;

import com.campus.util.springboot.log.Log;
import com.eggcampus.oms.api.manager.OmsFeignManager;
import com.eggcampus.oms.api.pojo.dto.UploadTokenDto;
import com.eggcampus.oms.api.pojo.qo.DeleteObjectQo;
import com.eggcampus.oms.api.pojo.qo.UploadTokenGenerationQo;
import com.eggcampus.oms.api.pojo.qo.UseObjectQo;
import com.eggcampus.oms.server.service.ObjectService;
import com.eggcampus.util.result.ReturnResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author 黄磊
 */
@RestController
public class ObjectController implements OmsFeignManager {
    @Resource
    private ObjectService objectService;

    @Log("生成图像的上传凭证")
    @Override
    public ReturnResult generateUploadToken(@Validated @RequestBody UploadTokenGenerationQo qo) {
        UploadTokenDto uploadToken = objectService.generateImageUploadToken(qo);
        return ReturnResult.success(uploadToken);
    }

    @Log("OSS资源上传回调")
    @PostMapping(ObjectService.OSS_CALLBACK_PATH)
    public String ossCallback(HttpServletRequest request) {
        return objectService.handleOssCallback(request);
    }


    @Log("使用资源")
    @Override
    public ReturnResult use(@Validated @RequestBody UseObjectQo qo) {
        objectService.use(qo);
        return ReturnResult.success("使用资源成功");
    }

    @Log("删除资源")
    @Override
    public ReturnResult delete(@Validated @RequestBody DeleteObjectQo qo) {
        objectService.delete(qo);
        return ReturnResult.success("删除资源成功");
    }
}
