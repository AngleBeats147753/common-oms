package com.eggcampus.object.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 黄磊
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadTokenDTO {
    private String key;
    private String token;
    private String url;
}
