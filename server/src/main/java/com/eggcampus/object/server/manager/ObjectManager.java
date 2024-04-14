package com.eggcampus.object.server.manager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eggcampus.object.server.pojo.ObjectDO;
import com.eggcampus.object.server.pojo.ObjectDO.UsageStatus;
import com.eggcampus.util.spring.mybatisplus.exception.NotFoundException;

/**
 * @author 黄磊
 */
public interface ObjectManager extends IService<ObjectDO> {
    /**
     * 通过url获取资源对象
     *
     * @param url url
     * @return {@link ObjectDO}
     */
    ObjectDO getByURL(String url);

    /**
     * 通过url获取资源对象
     *
     * @param url url
     * @return {@link ObjectDO}
     * @throws NotFoundException 未找到资源对象
     */
    ObjectDO findByURL(String url) throws NotFoundException;

    /**
     * 通过URL断言资源对象不存在
     *
     * @param url url
     */
    void assertNonExistenceByURL(String url);

    /**
     * 通过URL断言资源对象存在
     *
     * @param url url
     */
    void assertExistenceByURL(String url);

    /**
     * 通过URL断言资源对象的使用状态
     * 
     * @param url        url
     * @param usageStatus 使用状态
     */
    void assertUsageStatusByURL(String url, UsageStatus usageStatus);
}
