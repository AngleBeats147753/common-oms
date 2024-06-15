package com.eggcampus.oms.server.manager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eggcampus.oms.api.pojo.ObjectDO;
import com.eggcampus.oms.api.pojo.ObjectDO.UsageStatus;

import java.util.Collection;
import java.util.List;

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
     * @return 资源对象
     * @throws com.eggcampus.util.exception.database.NotFoundException 未找到资源对象时抛出这个异常
     */
    ObjectDO findByURL(String url);

    /**
     * 通过一组url获取一组资源对象
     *
     * @param urls 一组url
     * @return 资源对象
     */
    List<ObjectDO> listByURL(Collection<String> urls);

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
     * 断言资源对象的使用状态
     *
     * @param objectDO    资源对象
     * @param usageStatus 使用状态
     */
    void assertUsageStatus(ObjectDO objectDO, UsageStatus usageStatus);

    /**
     * 通过URL断言资源对象的使用状态
     *
     * @param url         url
     * @param usageStatus 使用状态
     */
    void assertUsageStatusByURL(String url, UsageStatus usageStatus);
}
