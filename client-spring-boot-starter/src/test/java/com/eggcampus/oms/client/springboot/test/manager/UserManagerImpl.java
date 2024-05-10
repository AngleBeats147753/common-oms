package com.eggcampus.oms.client.springboot.test.manager;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eggcampus.oms.client.springboot.test.pojo.User;
import com.eggcampus.oms.client.springboot.test.dao.UserMapper;
import org.springframework.stereotype.Service;

/**
 * @author 黄磊
 */
@Service
public class UserManagerImpl extends ServiceImpl<UserMapper, User> implements UserManager {
}
