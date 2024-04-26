package com.eggcampus.oms.server.pojo.qo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author 黄磊
 */
@Data
public class DeleteQO {
    @NotEmpty(message = "资源对象url不能为空")
    private String objectUrl;
}
