package com.eggcampus.image.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Oss配置
 *
 * @author 黄磊
 **/
@Configuration
public class OssConfig {
    @Bean
    public QiniuyunConfig qiniuyunConfig() {
        return new QiniuyunConfig();
    }
}
