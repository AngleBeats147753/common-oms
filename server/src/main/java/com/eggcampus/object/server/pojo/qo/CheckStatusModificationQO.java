package com.eggcampus.object.server.pojo.qo;

import com.eggcampus.object.server.pojo.ObjectDO;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.eggcampus.object.server.pojo.ObjectDO.*;

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
