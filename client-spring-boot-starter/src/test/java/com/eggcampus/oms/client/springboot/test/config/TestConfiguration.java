package com.eggcampus.oms.client.springboot.test.config;

import com.eggcampus.oms.client.springboot.EnableOms;
import com.eggcampus.oms.client.springboot.OmsManager;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * @author 黄磊
 */
@EnableOms
@Configuration
//@MapperScan("com.eggcampus.oms.client.springboot.test.dao")
public class TestConfiguration {
    @Autowired
    private OmsManager omsManager;
    @Lazy
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

//    @Bean
//    public MybatisPlusInterceptor mybatisPlusInterceptor() {
//        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
//        interceptor.addInnerInterceptor(new OmsInnerInterceptor(omsManager, sqlSessionTemplate));
//        return interceptor;
//    }

}
