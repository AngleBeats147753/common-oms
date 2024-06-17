package com.eggcampus.oms.api.pojo.qo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.eggcampus.oms.api.pojo.ApplicationDo.ShareLevel;

/**
 * @author 黄磊
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateApplicationQo {
    @Length(max = 50, message = "projectName长度不能超过50")
    @NotEmpty(message = "projectName不能为null")
    private String projectName;

    @Length(max = 50, message = "profile长度不能超过50")
    @NotEmpty(message = "profile不能为null")
    private String profile;

    @Length(max = 50, message = "pathPrefix长度不能超过50")
    @NotEmpty(message = "pathPrefix不能为null")
    private String pathPrefix;

    @NotNull(message = "shareLevel不能为null")
    private ShareLevel shareLevel;
}
