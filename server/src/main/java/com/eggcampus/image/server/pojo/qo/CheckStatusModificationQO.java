package com.eggcampus.image.server.pojo.qo;

import com.eggcampus.image.server.pojo.ImageDO.CheckStatus;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author 黄磊
 */
@Data
public class CheckStatusModificationQO {
    @NotEmpty(message = "imageURL不能为null或者空字符串")
    private String imageURL;

    @NotNull(message = "checkStatus不能为null")
    private CheckStatus checkStatus;
}
