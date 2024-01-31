package com.eggcampus.image.server.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eggcampus.image.server.dao.ApplicationDao;
import com.eggcampus.image.server.manager.ApplicationManager;
import com.eggcampus.image.server.pojo.ApplicationDO;
import com.eggcampus.util.spring.mybatisplus.exception.NotFoundException;
import com.eggcampus.util.spring.mybatisplus.service.VersionServiceImpl;
import lombok.NonNull;
import org.springframework.stereotype.Service;

/**
 * @author 黄磊
 */
@Service
public class ApplicationManagerImpl extends VersionServiceImpl<ApplicationDao, ApplicationDO> implements ApplicationManager {
    @Override
    public ApplicationDO getByNameAndProfile(String name, String profile) {
        QueryWrapper<ApplicationDO> wrapper = new QueryWrapper<ApplicationDO>()
                .eq(ApplicationDO.NAME, name)
                .eq(ApplicationDO.PROFILE, profile);
        return getOne(wrapper);
    }

    @Override
    public ApplicationDO findByNameAndProfile(@NonNull String name, @NonNull String profile) {
        ApplicationDO applicationDO = getByNameAndProfile(name, profile);
        if (applicationDO == null) {
            throw new NotFoundException("应用不存在，name<%s>, profile<%s>".formatted(name, profile));
        }
        return applicationDO;
    }
}
