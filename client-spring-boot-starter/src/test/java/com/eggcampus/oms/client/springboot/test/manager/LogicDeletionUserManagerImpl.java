package com.eggcampus.oms.client.springboot.test.manager;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eggcampus.oms.client.springboot.test.dao.LogicDeletionUserMapper;
import com.eggcampus.oms.client.springboot.test.pojo.LogicDeletionUser;
import org.springframework.stereotype.Service;

/**
 * @author 黄磊
 */
@Service
public class LogicDeletionUserManagerImpl extends ServiceImpl<LogicDeletionUserMapper, LogicDeletionUser> implements LogicDeletionUserManager {
}
