package com.eggcampus.object.server.manager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eggcampus.object.server.pojo.ObjectDO;
import com.eggcampus.util.spring.mybatisplus.exception.NotFoundException;

/**
 * @author 黄磊
 */
public interface ObjectManager extends IService<ObjectDO> {
    /**
     * 通过url获取图像
     *
     * @param url url
     * @return {@link ObjectDO}
     */
    ObjectDO getByURL(String url);

    /**
     * 通过url获取图像
     *
     * @param url url
     * @return {@link ObjectDO}
     * @throws NotFoundException 未找到图像
     */
    ObjectDO findByURL(String url) throws NotFoundException;
}
