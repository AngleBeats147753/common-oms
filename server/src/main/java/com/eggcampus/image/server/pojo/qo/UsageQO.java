package com.eggcampus.image.server.pojo.qo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author 黄磊
 */
@Data
public class UsageQO {
    @NotEmpty(message = "imageURL不能为null或者空字符串")
    private String imageURL;

    @NotNull(message = "needCheck不能为null")
    private Boolean needCheck;
}
