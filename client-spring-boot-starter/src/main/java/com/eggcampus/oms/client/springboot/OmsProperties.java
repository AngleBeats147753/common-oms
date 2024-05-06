package com.eggcampus.oms.client.springboot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

/**
 * @author 黄磊
 */
@Data
@Validated
@ConfigurationProperties(prefix = "eggcampus.oms")
public class OmsProperties {
    @NotEmpty(message = "url不能为空")
    private String url;
}
