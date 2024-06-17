package com.eggcampus.oms.api.manager;

import com.eggcampus.oms.api.pojo.qo.CreateApplicationQo;
import com.eggcampus.util.result.ReturnResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 黄磊
 */
public interface ApplicationFeignManager {
    @PostMapping("/application")
    ReturnResult createApplication(@Validated @RequestBody CreateApplicationQo qo);

    @PostMapping("/application/info/get")
    ReturnResult getApplication(@RequestParam("projectName") String projectName,
                                @RequestParam("profile") String profile);
}
