package com.eggcampus.object.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author huangshuaijie
 * @date 2024/2/17 10:53
 */
@Data
@ConfigurationProperties(prefix = ImageProperties.PREFIX)
public class ImageProperties {
    public static final String PREFIX = "common.image";

    /**
     * host
     */
    private String host = "localhost";


    /**
     * needCheck
     */
    private boolean needCheck = false;

}



