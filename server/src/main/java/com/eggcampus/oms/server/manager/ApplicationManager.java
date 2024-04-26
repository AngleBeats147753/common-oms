package com.eggcampus.oms.server.manager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eggcampus.oms.server.pojo.ApplicationDO;

/**
 * @author 黄磊
 */
public interface ApplicationManager extends IService<ApplicationDO> {
    /**
     * 根据名称和环境查找应用
     *
     * @param name    名称
     * @param profile 环境
     * @return 应用
     */
    ApplicationDO getByNameAndProfile(String name, String profile);

    /**
     * 根据名称和环境查找应用
     *
     * @param name    名称
     * @param profile 环境
     * @return 应用
     */
    ApplicationDO findByNameAndProfile(String name, String profile);
}
