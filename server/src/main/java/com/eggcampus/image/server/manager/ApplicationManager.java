package com.eggcampus.image.server.manager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eggcampus.image.server.pojo.ApplicationDO;
import com.eggcampus.util.spring.mybatisplus.exception.NotFoundException;

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
     * @exception NotFoundException 未找到应用
     */
    ApplicationDO findByNameAndProfile(String name, String profile) throws NotFoundException;
}
