package com.eggcampus.object.server.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eggcampus.object.server.dao.ObjectDao;
import com.eggcampus.object.server.manager.ObjectManager;
import com.eggcampus.object.server.pojo.ObjectDO;
import com.eggcampus.util.spring.mybatisplus.exception.NotFoundException;
import com.eggcampus.util.spring.mybatisplus.service.VersionServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.eggcampus.object.server.pojo.ObjectDO.*;

/**
 * @author 黄磊
 */
@Slf4j
@Service
public class ObjectManagerImpl extends VersionServiceImpl<ObjectDao, ObjectDO> implements ObjectManager {
    @Override
    public ObjectDO getByURL(String url) {
        return getOne(new QueryWrapper<ObjectDO>().eq(URL, url));
    }

    @Override
    public ObjectDO findByURL(String url) {
        ObjectDO objectDO = getByURL(url);
        if (objectDO == null) {
            throw new NotFoundException("对象不存在，url<%s>".formatted(url));
        }
        return objectDO;
    }

    @Override
    public void assertNonExistenceByURL(String url) {
        ObjectDO objectDO = getByURL(url);
        if (objectDO != null) {
            throw new NotFoundException("对象已存在，url<%s>".formatted(url));
        }
    }

    @Override
    public void assertExistenceByURL(String url) {
        ObjectDO objectDO = getByURL(url);
        if (objectDO == null) {
            throw new NotFoundException("对象不存在，url<%s>".formatted(url));
        }
    }

    @Override
    public void assertUsageStatus(ObjectDO objectDO, UsageStatus usageStatus) {
        if (!objectDO.getUsageStatus().equals(usageStatus)) {
            throw new IllegalArgumentException("对象使用状态不正确，id<%s>，期望usageStatus<%s>，当前usageStatus<%s>".formatted(objectDO.getId(), usageStatus, objectDO.getUsageStatus()));
        }
    }

    @Override
    public void assertUsageStatusByURL(String url, UsageStatus usageStatus) {
        ObjectDO objectDO = findByURL(url);
        assertUsageStatus(objectDO, usageStatus);
    }

    @Override
    public void assertCheckStatusByURL(String url, CheckStatus checkStatus) {
        ObjectDO objectDO = findByURL(url);
        if (!objectDO.getCheckStatus().equals(checkStatus)) {
            throw new IllegalArgumentException("对象审核状态不正确，id<%s>，期望usageStatus<%s>，当前usageStatus<%s>".formatted(objectDO.getId(), checkStatus, objectDO.getCheckStatus()));
        }
    }
}
