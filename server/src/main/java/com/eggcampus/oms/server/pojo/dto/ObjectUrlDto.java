package com.eggcampus.oms.server.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 黄磊
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObjectUrlDto {
    private String key;
    private String projectName;
    private String profile;
}
