package com.eggcampus.object.server.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eggcampus.object.server.dao.ObjectDao;
import com.eggcampus.object.server.manager.ObjectManager;
import com.eggcampus.object.server.pojo.ObjectDO;
import com.eggcampus.util.spring.mybatisplus.service.VersionServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author 黄磊
 */
@Service
public class ObjectManagerImpl extends VersionServiceImpl<ObjectDao, ObjectDO> implements ObjectManager {
    @Override
    public ObjectDO getByURL(String url) {
        return getOne(new QueryWrapper<ObjectDO>().eq(ObjectDO.URL, url));
    }

    @Override
    public ObjectDO findByURL(String url) {
        return getByURL(url);
    }
}
