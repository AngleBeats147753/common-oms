package com.eggcampus.image.server.manager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eggcampus.image.server.pojo.ImageDO;
import com.eggcampus.util.spring.mybatisplus.exception.NotFoundException;

/**
 * @author 黄磊
 */
public interface ImageManager extends IService<ImageDO> {
    /**
     * 通过url获取图像
     *
     * @param url url
     * @return {@link ImageDO}
     */
    ImageDO getByURL(String url);

    /**
     * 通过url获取图像
     *
     * @param url url
     * @return {@link ImageDO}
     * @throws NotFoundException 未找到图像
     */
    ImageDO findByURL(String url) throws NotFoundException;
}
