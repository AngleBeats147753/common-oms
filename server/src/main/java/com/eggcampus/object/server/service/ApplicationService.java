package com.eggcampus.object.server.service;

import com.eggcampus.object.server.manager.ApplicationManager;
import com.eggcampus.object.server.pojo.ApplicationDO;
import com.eggcampus.util.spring.application.ApplicationDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 黄磊
 */
@Service
public class ApplicationService {
    @Resource
    private ApplicationManager applicationManager;

    /**
     * 根据名称和环境查找应用
     *
     * @param dto 应用数据传输对象
     * @return 应用
     */
    public ApplicationDO findApplication(ApplicationDTO dto) {
        return applicationManager.findByNameAndProfile(dto.getName(), dto.getProfile());
    }
}
