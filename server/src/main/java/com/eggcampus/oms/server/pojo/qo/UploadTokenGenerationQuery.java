package com.eggcampus.oms.server.pojo.qo;

import com.campus.util.springboot.application.ApplicationDTO;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 存储生成上传凭证时所需的数据
 *
 * @author 黄磊
 */
@Data
public class UploadTokenGenerationQuery {
    @NotNull(message = "application不能为null")
    private ApplicationDTO application;

    @NotEmpty(message = "对象名不能为空")
    @Pattern(regexp = "^(?![.])[^ \"()<>{}|\\s/?=&#\\\\,]*$", message = "对象名不合法，不能英文逗号开始，并且不能包含空格、引号、括号、尖括号、花括号、竖线、问号、等号、井号、反引号、英文逗号，假如非要这些字符的话，可以用URL编码")
    private String imageName;
}
