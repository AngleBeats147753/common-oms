package com.eggcampus.object.server.controller;

import com.eggcampus.object.server.pojo.qo.*;
import com.eggcampus.object.server.service.ObjectService;
import com.eggcampus.util.result.ReturnResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import static com.eggcampus.object.server.pojo.ObjectDO.CheckStatus.CHECK_FAILED;
import static com.eggcampus.object.server.pojo.ObjectDO.CheckStatus.CHECK_SUCCESS;
import static com.eggcampus.util.result.AliErrorCode.USER_ERROR_A0402;

/**
 * @author 黄磊
 */
@RestController
public class ObjectController {
    @Resource
    private ObjectService objectService;

    @PostMapping("/image/upload-token")
    public ReturnResult uploadToken(@Validated @RequestBody UploadTokenGenerationQO qo) {
        return objectService.generateUploadToken(qo);
    }

    @PutMapping("/object/use")
    public ReturnResult useObject(@Validated @RequestBody UsageQO qo) {
        objectService.use(qo);
        return ReturnResult.getSuccessReturn("使用图像成功");
    }

    @PutMapping("/object/status")
    public ReturnResult checkObject(@Validated @RequestBody CheckStatusModificationQO qo) {
        if (!CHECK_FAILED.equals(qo.getCheckStatus()) && !CHECK_SUCCESS.equals(qo.getCheckStatus())) {
            return ReturnResult.getFailureReturn(USER_ERROR_A0402, "审核状态不能为<%s>".formatted(qo.getCheckStatus().getName()));
        }
        objectService.modifyCheckStatus(qo);
        return ReturnResult.getSuccessReturn("更改图像审核状态成功");
    }

    // TODO 删除返回的状态一定是正常的，再大的错误也不能返回给客户端，调用该接口后代表服务器种没有这个图像了
    @DeleteMapping("/object")
    public ReturnResult deleteObject(@Validated DeleteQO qo) {
        objectService.delete(qo);
        return ReturnResult.getSuccessReturn("删除图像成功");
    }

    @PutMapping("/object/useList")
    public ReturnResult useObjectList(@Validated @RequestBody BatchUsageQO qo) {
        objectService.useObjectList(qo.getUsageQoList());
        return ReturnResult.getSuccessReturn("使用对象列表成功");
    }

    @DeleteMapping("/object/removeList")
    public ReturnResult deleteObjectList(@Validated @RequestBody BatchDeleteQO deleteQO) {
        objectService.deleteObjectList(deleteQO.getDeleteQoList());
        return ReturnResult.getSuccessReturn("删除对象列表成功");
    }
}
