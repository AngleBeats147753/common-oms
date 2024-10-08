package com.eggcampus.oms.server.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.util.springboot.mybatisplus.EggCampusServiceImpl;
import com.eggcampus.oms.api.pojo.ObjectDo;
import com.eggcampus.oms.server.dao.ObjectDao;
import com.eggcampus.oms.server.manager.ObjectManager;
import com.eggcampus.util.exception.database.NotFoundException;
import com.eggcampus.util.exception.result.NonLoggingManagerException;
import com.eggcampus.util.result.AliErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static com.eggcampus.oms.api.pojo.ObjectDo.URL;
import static com.eggcampus.oms.api.pojo.ObjectDo.UsageStatus;

/**
 * @author 黄磊
 */
@Slf4j
@Service
public class ObjectManagerImpl extends EggCampusServiceImpl<ObjectDao, ObjectDo> implements ObjectManager {
    @Override
    public ObjectDo getByURL(String url) {
        return getOne(new QueryWrapper<ObjectDo>().eq(URL, url));
    }

    @Override
    public ObjectDo findByURL(String url) {
        ObjectDo objectDO = getByURL(url);
        if (objectDO == null) {
            throw new NotFoundException("对象不存在，url<%s>".formatted(url));
        }
        return objectDO;
    }

    @Override
    public List<ObjectDo> listByURL(Collection<String> urls) {
        return query().in(URL, new HashSet<>(urls)).list();
    }

    @Override
    public void assertNonExistenceByURL(String url) {
        ObjectDo objectDO = getByURL(url);
        if (objectDO != null) {
            throw new NotFoundException("对象已存在，url<%s>".formatted(url));
        }
    }

    @Override
    public void assertExistenceByURL(String url) {
        ObjectDo objectDO = getByURL(url);
        if (objectDO == null) {
            throw new NotFoundException("对象不存在，url<%s>".formatted(url));
        }
    }

    @Override
    public void assertUsageStatus(ObjectDo objectDO, UsageStatus usageStatus) {
        if (!objectDO.getUsageStatus().equals(usageStatus)) {
            throw new NonLoggingManagerException(AliErrorCode.USER_ERROR_A0402, "对象使用状态不正确，id<%s>，期望usageStatus<%s>，当前usageStatus<%s>".formatted(objectDO.getId(), usageStatus, objectDO.getUsageStatus()));
        }
    }

    @Override
    public void assertUsageStatusByURL(String url, UsageStatus usageStatus) {
        ObjectDo objectDO = findByURL(url);
        assertUsageStatus(objectDO, usageStatus);
    }
}
