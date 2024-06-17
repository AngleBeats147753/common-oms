package com.eggcampus.oms.api.pojo.dto;

import com.eggcampus.oms.api.pojo.ApplicationDo;
import com.eggcampus.oms.api.pojo.ApplicationDo.ShareLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 黄磊
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OmsApplicationDto {
    private Long applicationId;
    private String projectName;
    private String profile;
    private String pathPrefix;
    private ShareLevel shareLevel;

    public OmsApplicationDto(ApplicationDo application) {
        this.applicationId = application.getId();
        this.projectName = application.getProjectName();
        this.profile = application.getProfile();
        this.pathPrefix = application.getPathPrefix();
        this.shareLevel = application.getShareLevel();
    }
}
