package com.eggcampus.image.server.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eggcampus.image.server.dao.ImageDao;
import com.eggcampus.image.server.manager.ImageManager;
import com.eggcampus.image.server.pojo.ImageDO;
import com.eggcampus.util.spring.mybatisplus.exception.NotFoundException;
import com.eggcampus.util.spring.mybatisplus.service.VersionServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author 黄磊
 */
@Service
public class ImageManagerImpl extends VersionServiceImpl<ImageDao, ImageDO> implements ImageManager {
    @Override
    public ImageDO getByURL(String url) {
        return getOne(new QueryWrapper<ImageDO>().eq(ImageDO.URL, url));
    }

    @Override
    public ImageDO findByURL(String url) {
        ImageDO imageDO = getByURL(url);
        if (imageDO == null) {
            throw new NotFoundException("图像不存在，url<%s>".formatted(url));
        }
        return imageDO;
    }
}
