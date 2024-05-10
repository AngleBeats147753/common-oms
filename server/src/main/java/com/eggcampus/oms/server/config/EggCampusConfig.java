package com.eggcampus.oms.server.config;

import com.campus.util.springboot.EnableEggCampusBasic;
import com.campus.util.springboot.seata.EnableEggCampusSeata;
import org.springframework.context.annotation.Configuration;

/**
 * @author 黄磊
 */
@Configuration
@EnableEggCampusBasic
@EnableEggCampusSeata
public class EggCampusConfig {
}
