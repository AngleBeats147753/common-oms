package com.eggcampus.oms.client.springboot.mybatis;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.eggcampus.oms.client.springboot.EnableOms;
import com.eggcampus.oms.client.springboot.OmsInnerInterceptor;
import com.eggcampus.oms.client.springboot.OmsManager;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 黄磊
 */
@EnableOms
@Configuration
@MapperScan("com.eggcampus.oms.client.springboot.mybatis.dao")
public class MybatisConfig {
    @Autowired
    private OmsManager omsManager;

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new OmsInnerInterceptor(omsManager));
        return interceptor;
    }

}
