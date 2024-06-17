package com.eggcampus.oms.api.pojo.qo;

import com.campus.util.springboot.application.EggCampusApplicationDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

/**
 * @author 黄磊
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetApplicationQo {
    @Length(max = 50, message = "projectName长度不能超过50")
    @NotEmpty(message = "projectName不能为null")
    private String projectName;

    @Length(max = 50, message = "profile长度不能超过50")
    @NotEmpty(message = "profile不能为null")
    private String profile;

    public GetApplicationQo(EggCampusApplicationDTO dto) {
        this.projectName = dto.getProjectName();
        this.profile = dto.getProfile();
    }
}
