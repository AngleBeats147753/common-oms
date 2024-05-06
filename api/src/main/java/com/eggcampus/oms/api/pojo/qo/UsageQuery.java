package com.eggcampus.oms.api.pojo.qo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author 黄磊
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsageQuery {
    @NotEmpty(message = "objectUrl不能为null或者空字符串")
    private String objectUrl;

    @NotNull(message = "needCheck不能为null")
    private Boolean needCheck;
}
