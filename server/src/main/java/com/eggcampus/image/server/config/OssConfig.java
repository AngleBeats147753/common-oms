package com.eggcampus.image.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Oss配置
 *
 * @author 黄磊
 **/
@Import(QiniuyunConfig.class)
@Configuration
public class OssConfig {
}
