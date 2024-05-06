package com.eggcampus.oms.server.service;

import com.campus.util.springboot.application.EggCampusApplicationDTO;
import com.eggcampus.oms.api.pojo.ApplicationDO;
import com.eggcampus.oms.server.manager.ApplicationManager;
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
    public ApplicationDO findApplication(EggCampusApplicationDTO dto) {
        return applicationManager.findByNameAndProfile(dto.getProjectName(), dto.getServiceName(), dto.getProfile());
    }
}
