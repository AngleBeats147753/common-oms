package com.eggcampus.oms.server.manager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eggcampus.oms.api.pojo.ApplicationDo;

import javax.annotation.Nullable;

/**
 * @author 黄磊
 */
public interface ApplicationManager extends IService<ApplicationDo> {
    /**
     * 根据id查找应用
     *
     * @param applicationId 应用id
     * @return 应用
     * @throws com.eggcampus.util.exception.database.NotFoundException 未找到时抛出
     */
    ApplicationDo findById(Long applicationId);

    /**
     * 根据名称和环境查找应用
     *
     * @param projectName 项目名
     * @param profile     环境
     * @return 应用
     */
    @Nullable
    ApplicationDo getByNameAndProfile(String projectName, String profile);

    /**
     * 根据名称和环境查找应用
     *
     * @param projectName 项目名
     * @param profile     环境
     * @return 应用
     * @throws com.eggcampus.util.exception.database.NotFoundException 未找到时抛出
     */
    ApplicationDo findByNameAndProfile(String projectName, String profile);

    void assertNonExistenceByNameAndProfile(String projectName, String profile);
}
