package com.eggcampus.image.server.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/**
 * 七牛云配置
 *
 * @author 黄磊
 */
@Import(QiniuyunSelector.class)
@EnableConfigurationProperties(QiniuyunProperties.class)
public class QiniuyunConfig {

}
