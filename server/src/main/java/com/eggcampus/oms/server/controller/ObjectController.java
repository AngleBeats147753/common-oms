package com.eggcampus.oms.server.controller;

import com.campus.util.springboot.log.Log;
import com.eggcampus.oms.api.manager.OmsFeignManager;
import com.eggcampus.oms.api.pojo.dto.UploadTokenDTO;
import com.eggcampus.oms.api.pojo.qo.DeletionQuery;
import com.eggcampus.oms.api.pojo.qo.UploadTokenGenerationQuery;
import com.eggcampus.oms.server.service.ObjectService;
import com.eggcampus.util.result.AliErrorCode;
import com.eggcampus.util.result.ReturnResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * @author 黄磊
 */
@RestController
public class ObjectController implements OmsFeignManager {
    @Resource
    private ObjectService objectService;

    @Log("生成图像的上传凭证")
    @Override
    public ReturnResult generateUploadToken(@Validated @RequestBody UploadTokenGenerationQuery qo) {
        UploadTokenDTO uploadToken = objectService.generateImageUploadToken(qo);
        return ReturnResult.getSuccessReturn(uploadToken);
    }

    @Log("OSS资源上传回调")
    @PostMapping(ObjectService.OSS_CALLBACK_PATH)
    public String ossCallback(HttpServletRequest request) {
        return objectService.handleOssCallback(request);
    }


    @Log("使用资源")
    @Override
    public ReturnResult use(@RequestBody Set<String> urls) {
        if (urls.isEmpty()) {
            return ReturnResult.getFailureReturn(AliErrorCode.USER_ERROR_A0400, "使用资源的数量不能为0");
        }
        objectService.use(urls);
        return ReturnResult.getSuccessReturn("使用资源成功");
    }

    @Log("删除资源")
    @Override
    public ReturnResult delete(@RequestBody Set<String> urls) {
        if (urls.isEmpty()) {
            return ReturnResult.getFailureReturn(AliErrorCode.USER_ERROR_A0400, "删除资源的数量不能为0");
        }
        objectService.delete(urls);
        return ReturnResult.getSuccessReturn("删除资源成功");
    }
}
