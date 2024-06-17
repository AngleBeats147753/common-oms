package com.eggcampus.oms.api.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 黄磊
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadTokenDto {
    private String key;
    private String token;
    private String url;
}
