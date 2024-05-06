package com.eggcampus.oms.server.manager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eggcampus.oms.api.pojo.ApplicationDO;

/**
 * @author 黄磊
 */
public interface ApplicationManager extends IService<ApplicationDO> {
    /**
     * 根据名称和环境查找应用
     *
     * @param projectName 项目名
     * @param serviceName 服务名
     * @param profile     环境
     * @return 应用
     */
    ApplicationDO getByNameAndProfile(String projectName, String serviceName, String profile);

    /**
     * 根据名称和环境查找应用
     *
     * @param projectName 项目名
     * @param serviceName 服务名
     * @param profile     环境
     * @return 应用
     */
    ApplicationDO findByNameAndProfile(String projectName, String serviceName, String profile);
}
