package com.eggcampus.oms.server.service;

import com.eggcampus.oms.api.pojo.ApplicationDo;
import com.eggcampus.oms.api.pojo.dto.OmsApplicationDto;
import com.eggcampus.oms.api.pojo.qo.CreateApplicationQo;
import com.eggcampus.oms.server.manager.ApplicationManager;
import com.eggcampus.util.result.ReturnResult;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author 黄磊
 */
@Service
@Slf4j
@Transactional
@GlobalTransactional
public class ApplicationWriteService {
    @Resource
    private ApplicationManager applicationManager;

    public ReturnResult createApplication(CreateApplicationQo qo) {
        applicationManager.assertNonExistenceByNameAndProfile(qo.getProjectName(), qo.getProfile());
        ApplicationDo application = new ApplicationDo();
        application.setProjectName(qo.getProjectName());
        application.setProfile(qo.getProfile());
        application.setPathPrefix(qo.getPathPrefix());
        application.setShareLevel(qo.getShareLevel());
        applicationManager.save(application);
        log.info("创建应用成功，applicationId<%s>，projectName<%s>，profile<%s>".formatted(application.getId(), application.getProjectName(), application.getProfile()));
        return ReturnResult.getSuccessReturn(new OmsApplicationDto(application));
    }

}
