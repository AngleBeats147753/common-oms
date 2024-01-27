package com.eggcampus.image.server.dao;

import com.eggcampus.image.server.pojo.ApplicationDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 应用 Mapper 接口
 * </p>
 *
 * @author 黄磊
 * @since 2024-01-28
 */
@Mapper
public interface ApplicationDao extends BaseMapper<ApplicationDO> {

}
