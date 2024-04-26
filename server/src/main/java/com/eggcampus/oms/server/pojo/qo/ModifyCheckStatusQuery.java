package com.eggcampus.oms.server.pojo.qo;

import com.eggcampus.oms.server.pojo.ObjectDO;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author 黄磊
 */
@Data
public class ModifyCheckStatusQuery {
    @NotEmpty(message = "imageUrl不能为null或者空字符串")
    private String imageUrl;

    @NotNull(message = "checkStatus不能为null")
    private ObjectDO.CheckStatus checkStatus;
}
