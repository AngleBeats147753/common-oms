package com.eggcampus.oms.client.springboot;

import com.campus.util.springboot.application.EggCampusApplicationManager;
import com.campus.util.springboot.application.EnableEggCampusApplication;
import com.campus.util.springboot.feign.EnableFeign;
import com.campus.util.springboot.seata.EnableSeata;
import com.eggcampus.oms.api.manager.OmsFeignManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeansException;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClientBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.lang.NonNull;

import javax.annotation.Resource;

/**
 * @author 黄磊
 */
@Configuration
@EnableFeign
@EnableSeata
@EnableFeignClients
@EnableEggCampusApplication
@Import(OmsProperties.class)
public class OmsAutoConfiguration implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    @Resource
    private OmsProperties omsProperties;
    @Resource
    private EggCampusApplicationManager applicationManager;
    @Resource
    private ObjectMapper objectMapper;

    @Bean
    public OmsFeignManager omsFeignManager() {
        FeignClientBuilder feignClientBuilder = new FeignClientBuilder(applicationContext);
        FeignClientBuilder.Builder<OmsFeignManager> builder = feignClientBuilder.forType(OmsFeignManager.class, "oms");
        builder.url(omsProperties.getUrl());
        return builder.build();
    }

    @Bean
    public OmsManager omsManager() {
        return new OmsManagerImpl(omsFeignManager(), applicationManager, objectMapper);
    }

//    @Bean
//    public InnerInterceptor omsMybatisInterceptor() {
//        return new OmsInnerInterceptor(omsManager());
//    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
