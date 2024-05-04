package com.eggcampus.oms.server.controller;

import com.campus.util.springboot.log.Log;
import com.eggcampus.oms.server.pojo.dto.UploadTokenDTO;
import com.eggcampus.oms.server.pojo.qo.ModifyCheckStatusQuery;
import com.eggcampus.oms.server.pojo.qo.UploadTokenGenerationQuery;
import com.eggcampus.oms.server.pojo.qo.UsageQuery;
import com.eggcampus.oms.server.service.ObjectService;
import com.eggcampus.util.result.AliErrorCode;
import com.eggcampus.util.result.ReturnResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.eggcampus.oms.server.pojo.ObjectDO.CheckStatus.CHECK_FAILED;
import static com.eggcampus.oms.server.pojo.ObjectDO.CheckStatus.CHECK_SUCCESS;
import static com.eggcampus.util.result.AliErrorCode.USER_ERROR_A0402;

/**
 * @author 黄磊
 */
@RestController
public class ObjectController {
    @Resource
    private ObjectService objectService;

    @Log("生成图像的上传凭证")
    @PostMapping("/image/upload-token")
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
    @PutMapping("/object/use")
    public ReturnResult use(@Validated @RequestBody List<UsageQuery> queries) {
        if (queries.isEmpty()) {
            return ReturnResult.getFailureReturn(AliErrorCode.USER_ERROR_A0400, "使用资源的数量不能为0");
        }
        objectService.use(queries);
        return ReturnResult.getSuccessReturn("使用资源成功");
    }

    @Log("删除资源")
    @DeleteMapping("/object/delete")
    public ReturnResult delete(@Validated @RequestBody List<String> urls) {
        if (urls.isEmpty()) {
            return ReturnResult.getFailureReturn(AliErrorCode.USER_ERROR_A0400, "删除资源的数量不能为0");
        }
        objectService.deleteTemporarily(urls);
        return ReturnResult.getSuccessReturn("删除资源成功");
    }

    @Log("更改审核状态")
    @PutMapping("/object/status")
    public ReturnResult checkObject(@Validated @RequestBody ModifyCheckStatusQuery qo) {
        if (!CHECK_FAILED.equals(qo.getCheckStatus()) && !CHECK_SUCCESS.equals(qo.getCheckStatus())) {
            return ReturnResult.getFailureReturn(USER_ERROR_A0402, "审核状态不能为<%s>".formatted(qo.getCheckStatus().getName()));
        }
        objectService.modifyCheckStatus(qo);
        return ReturnResult.getSuccessReturn("更改图像审核状态成功");
    }
}
