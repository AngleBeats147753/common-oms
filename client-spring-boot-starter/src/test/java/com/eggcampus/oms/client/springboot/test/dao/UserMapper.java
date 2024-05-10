package com.eggcampus.oms.client.springboot.test.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eggcampus.oms.client.springboot.test.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
