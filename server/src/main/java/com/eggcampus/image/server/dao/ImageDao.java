package com.eggcampus.image.server.dao;

import com.eggcampus.image.server.pojo.ImageDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 图像 Mapper 接口
 * </p>
 *
 * @author 黄磊
 * @since 2024-01-28
 */
@Mapper
public interface ImageDao extends BaseMapper<ImageDO> {

}
