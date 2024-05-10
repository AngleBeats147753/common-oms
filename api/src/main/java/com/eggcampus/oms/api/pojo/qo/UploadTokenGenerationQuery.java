package com.eggcampus.oms.api.pojo.qo;

import com.campus.util.springboot.application.EggCampusApplicationDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 存储生成上传凭证时所需的数据
 *
 * @author 黄磊
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadTokenGenerationQuery {
    @Valid
    @NotNull(message = "application不能为null")
    private EggCampusApplicationDTO application;

    @Length(max = 255, message = "imageName长度不能超过255")
    @NotEmpty(message = "对象名不能为空")
    @Pattern(regexp = "^(?![.])[^ \"()<>{}|\\s/?=&#\\\\,]*$", message = "对象名不合法，不能英文逗号开始，并且不能包含空格、引号、括号、尖括号、花括号、竖线、问号、等号、井号、反引号、英文逗号，假如非要这些字符的话，可以用URL编码")
    private String imageName;
}
