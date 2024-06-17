package com.eggcampus.oms.api.pojo.qo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;

/**
 * @author 黄磊
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UseObjectQo {
    @NotNull(message = "applicationId不能为null")
    private Long applicationId;

    @NotNull(message = "urls不能为null")
    @Size(min = 1, message = "urls不能为空")
    private Collection<String> urls;
}
