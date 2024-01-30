package com.eggcampus.image.server.controller;

import com.eggcampus.image.server.pojo.qo.CheckStatusModificationQO;
import com.eggcampus.image.server.pojo.qo.DeleteQO;
import com.eggcampus.image.server.pojo.qo.UploadTokenGenerationQO;
import com.eggcampus.image.server.pojo.qo.UsageQO;
import com.eggcampus.image.server.service.ImageService;
import com.eggcampus.util.result.ReturnResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import static com.eggcampus.image.enums.CheckStatus.CHECK_FAILED;
import static com.eggcampus.image.enums.CheckStatus.CHECK_SUCCESS;
import static com.eggcampus.util.result.AliErrorCode.USER_ERROR_A0402;

/**
 * @author 黄磊
 */
@RestController
public class ImageController {
    @Resource
    private ImageService imageService;

    @PostMapping("/image/upload-token")
    public ReturnResult uploadToken(@Validated @RequestBody UploadTokenGenerationQO qo) {
        return imageService.generateUploadToken(qo);
    }

    @PutMapping("/image/use")
    public ReturnResult useImage(@Validated @RequestBody UsageQO qo) {
        imageService.use(qo);
        return ReturnResult.getSuccessReturn("使用图像成功");
    }

    @PutMapping("/image/status")
    public ReturnResult checkImage(@Validated @RequestBody CheckStatusModificationQO qo) {
        if (!CHECK_FAILED.equals(qo.getCheckStatus()) && !CHECK_SUCCESS.equals(qo.getCheckStatus())) {
            return ReturnResult.getFailureReturn(USER_ERROR_A0402, "审核状态不能为<%s>".formatted(qo.getCheckStatus().getName()));
        }
        imageService.modifyCheckStatus(qo);
        return ReturnResult.getSuccessReturn("更改图像审核状态成功");
    }

    // TODO 删除返回的状态一定是正常的，再大的错误也不能返回给客户端，调用该接口后代表服务器种没有这个图像了
    @DeleteMapping("/image")
    public ReturnResult deleteImage(@Validated DeleteQO qo) {
        imageService.delete(qo);
        return ReturnResult.getSuccessReturn("删除图像成功");
    }
}
