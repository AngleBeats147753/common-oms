package com.eggcampus.oms.server.controller;

import com.campus.util.springboot.log.Log;
import com.campus.util.springboot.mybatisplus.PageQo;
import com.eggcampus.oms.api.manager.ApplicationFeignManager;
import com.eggcampus.oms.api.pojo.qo.CreateApplicationQo;
import com.eggcampus.oms.api.pojo.qo.GetApplicationQo;
import com.eggcampus.oms.server.service.ApplicationReadService;
import com.eggcampus.oms.server.service.ApplicationWriteService;
import com.eggcampus.util.result.ReturnResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 黄磊
 */
@RestController
public class ApplicationController implements ApplicationFeignManager {
    @Resource
    private ApplicationReadService applicationReadService;
    @Resource
    private ApplicationWriteService applicationWriteService;

    @Log("创建应用")
    public ReturnResult createApplication(@Validated @RequestBody CreateApplicationQo qo) {
        return applicationWriteService.createApplication(qo);
    }

    @Log("获取应用")
    public ReturnResult getApplication(@RequestParam("projectName") String projectName,
                                       @RequestParam("profile") String profile) {
        GetApplicationQo qo = new GetApplicationQo(projectName, profile);
        return applicationReadService.getApplication(qo);
    }

    @Log("获取应用列表")
    @GetMapping("/application/infos")
    public ReturnResult getApplications(@Validated PageQo qo) {
        return applicationReadService.getApplications(qo);
    }

}
