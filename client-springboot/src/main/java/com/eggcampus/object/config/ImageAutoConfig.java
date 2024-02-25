package com.eggcampus.object.config;

import com.eggcampus.object.ObjectClient;
import com.eggcampus.object.manager.ObjectManager;
import com.eggcampus.object.manager.impl.ObjectManagerImpl;
import com.eggcampus.util.spring.application.ApplicationManager;
import com.eggcampus.util.spring.application.EnableApplicationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 黄磊
 */
@Configuration
@EnableApplicationManager
@EnableConfigurationProperties({ImageProperties.class})
@RequiredArgsConstructor
public class ImageAutoConfig {

    private final ImageProperties imageProperties;
    private final ApplicationManager applicationManager;

    @Bean
    @ConditionalOnMissingBean
    public ObjectClient objectClient() {
        return new ObjectClient(imageProperties.getHost(), imageProperties.isNeedCheck());
    }

    @Bean
    public ObjectManager objectManager(ObjectClient objectClient) {
        return new ObjectManagerImpl(objectClient, applicationManager);
    }
}
