package com.eggcampus.oms.server.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.util.springboot.mybatisplus.EggCampusServiceImpl;
import com.eggcampus.oms.api.pojo.ApplicationDo;
import com.eggcampus.oms.server.dao.ApplicationDao;
import com.eggcampus.oms.server.manager.ApplicationManager;
import com.eggcampus.util.exception.base.AssertionFailedException;
import com.eggcampus.util.exception.database.NotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author 黄磊
 */
@Service
public class ApplicationManagerImpl extends EggCampusServiceImpl<ApplicationDao, ApplicationDo> implements ApplicationManager {
    @Override
    public ApplicationDo findById(Long applicationId) {
        ApplicationDo applicationDO = getById(applicationId);
        if (applicationDO == null) {
            throw new NotFoundException("应用不存在，applicationId<%s>".formatted(applicationId));
        }
        return applicationDO;
    }

    @Override
    public ApplicationDo getByNameAndProfile(String projectName, String profile) {
        QueryWrapper<ApplicationDo> wrapper = new QueryWrapper<ApplicationDo>()
                .eq(ApplicationDo.PROJECT_NAME, projectName)
                .eq(ApplicationDo.PROFILE, profile);
        return getOne(wrapper);
    }

    @Override
    public ApplicationDo findByNameAndProfile(String projectName, String profile) {
        ApplicationDo applicationDO = getByNameAndProfile(projectName, profile);
        if (applicationDO == null) {
            throw new NotFoundException("应用不存在，projectName<%s>，profile<%s>".formatted(projectName, profile));
        }
        return applicationDO;
    }

    @Override
    public void assertNonExistenceByNameAndProfile(String projectName, String profile) {
        ApplicationDo applicationDO = getByNameAndProfile(projectName, profile);
        if (applicationDO != null) {
            throw new AssertionFailedException("应用已存在，projectName<%s>，profile<%s>".formatted(projectName, profile));
        }
    }
}
