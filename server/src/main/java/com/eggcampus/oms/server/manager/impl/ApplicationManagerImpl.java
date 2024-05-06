package com.eggcampus.oms.server.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.util.springboot.mybatisplus.EggCampusServiceImpl;
import com.eggcampus.oms.api.pojo.ApplicationDO;
import com.eggcampus.oms.server.dao.ApplicationDao;
import com.eggcampus.oms.server.manager.ApplicationManager;
import com.eggcampus.util.exception.database.NotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author 黄磊
 */
@Service
public class ApplicationManagerImpl extends EggCampusServiceImpl<ApplicationDao, ApplicationDO> implements ApplicationManager {
    @Override
    public ApplicationDO getByNameAndProfile(String projectName, String serviceName, String profile) {
        QueryWrapper<ApplicationDO> wrapper = new QueryWrapper<ApplicationDO>()
                .eq(ApplicationDO.PROJECT_NAME, projectName)
                .eq(ApplicationDO.SERVICE_NAME, serviceName)
                .eq(ApplicationDO.PROFILE, profile);
        return getOne(wrapper);
    }

    @Override
    public ApplicationDO findByNameAndProfile(String projectName, String serviceName, String profile) {
        ApplicationDO applicationDO = getByNameAndProfile(projectName, serviceName, profile);
        if (applicationDO == null) {
            throw new NotFoundException("应用不存在，projectName<%s>，serviceName<%s> ，profile<%s>".formatted(projectName, serviceName, profile));
        }
        return applicationDO;
    }
}
