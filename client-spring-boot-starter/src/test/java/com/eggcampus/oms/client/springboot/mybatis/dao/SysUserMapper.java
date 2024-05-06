package com.eggcampus.oms.client.springboot.mybatis.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eggcampus.oms.client.springboot.mybatis.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

}
