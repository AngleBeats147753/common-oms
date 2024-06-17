package com.eggcampus.oms.client.springboot;

import com.campus.util.springboot.application.EggCampusApplicationDTO;
import com.campus.util.springboot.application.EggCampusApplicationManager;
import com.campus.util.springboot.application.EnableEggCampusApplication;
import com.campus.util.springboot.feign.EnableEggCampusFeign;
import com.campus.util.springboot.seata.EnableEggCampusSeata;
import com.eggcampus.oms.api.manager.ApplicationFeignManager;
import com.eggcampus.oms.api.manager.OmsFeignManager;
import com.eggcampus.oms.api.pojo.ApplicationDo;
import com.eggcampus.oms.api.pojo.dto.OmsApplicationDto;
import com.eggcampus.oms.api.pojo.qo.CreateApplicationQo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.plugin.Interceptor;
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
import java.util.HashMap;

/**
 * @author 黄磊
 */
@Configuration
@EnableEggCampusFeign
@EnableEggCampusSeata
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
    public ApplicationFeignManager applicationFeignManager() {
        FeignClientBuilder feignClientBuilder = new FeignClientBuilder(applicationContext);
        FeignClientBuilder.Builder<ApplicationFeignManager> builder = feignClientBuilder.forType(ApplicationFeignManager.class, "oms1");
        builder.url(omsProperties.getUrl());
        return builder.build();
    }

    @Bean
    public OmsManager omsManager() {
        // TODO 为自动注册增加测试代码，并且要使用dubbo而不是feign
        ApplicationFeignManager applicationFeignManager = applicationFeignManager();
        EggCampusApplicationDTO eca = applicationManager.getApplication();
        HashMap<String, Object> map = (HashMap<String, Object>) applicationFeignManager.getApplication(eca.getProjectName(), eca.getProfile()).getData();
        OmsApplicationDto omsApplicationDto;
        if (map == null) {
            CreateApplicationQo createApplicationQo = CreateApplicationQo.builder()
                    .projectName(eca.getProjectName())
                    .profile(eca.getProfile())
                    .pathPrefix("%s/%s/".formatted(eca.getProjectName(), eca.getProfile()))
                    .shareLevel(ApplicationDo.ShareLevel.NONE)
                    .build();
            map = (HashMap<String, Object>) applicationFeignManager.createApplication(createApplicationQo).getData();
        }
        omsApplicationDto = convert2OmsApplicationDto(map);
        return new OmsManagerImpl(omsFeignManager(), omsApplicationDto, objectMapper);
    }

    private OmsApplicationDto convert2OmsApplicationDto(HashMap<String, Object> map) {
        OmsApplicationDto omsApplicationDto = new OmsApplicationDto();
        omsApplicationDto.setApplicationId(((Integer) map.get("applicationId")).longValue());
        omsApplicationDto.setProjectName((String) map.get("projectName"));
        omsApplicationDto.setProfile((String) map.get("profile"));
        omsApplicationDto.setPathPrefix((String) map.get("pathPrefix"));
        ApplicationDo.ShareLevel shareLevel = switch ((String) map.get("shareLevel")) {
            case "不共享" -> ApplicationDo.ShareLevel.NONE;
            case "环境" -> ApplicationDo.ShareLevel.PROFILE;
            default -> throw new RuntimeException("shareLevel不合法");
        };
        omsApplicationDto.setShareLevel(shareLevel);
        return omsApplicationDto;
    }

    @Bean
    public Interceptor interceptor() {
        return new OmsInterceptor(omsManager());
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
