package com.eggcampus.object.annotations;

import com.eggcampus.object.config.ObjectClientConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author huangshuaijie
 * @date 2024/2/25 16:11
 */
@Import(ObjectClientConfiguration.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableObjectClient {
}
