package com.eggcampus.oms.client.springboot;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author 黄磊
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({OmsAutoConfiguration.class})
public @interface EnableOms {
}
