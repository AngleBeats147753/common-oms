package com.eggcampus.object;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author huangshuaijie
 * @date 2024/2/15 12:51
 */
@Data
public class UsageQO {
    @NotEmpty(message = "imageURL不能为null或者空字符串")
    private String imageURL;

    @NotNull(message = "needCheck不能为null")
    private Boolean needCheck;
}
