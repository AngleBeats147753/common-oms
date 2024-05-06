package com.eggcampus.oms.api.pojo.qo;

import com.eggcampus.oms.api.pojo.ObjectDO;
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
public class ModifyCheckStatusQuery {
    @NotEmpty(message = "imageUrl不能为null或者空字符串")
    private String imageUrl;

    @NotNull(message = "checkStatus不能为null")
    private ObjectDO.CheckStatus checkStatus;
}
