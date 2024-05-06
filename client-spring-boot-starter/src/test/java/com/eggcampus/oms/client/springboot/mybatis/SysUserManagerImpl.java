package com.eggcampus.oms.client.springboot.mybatis;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eggcampus.oms.client.springboot.mybatis.dao.SysUserMapper;
import org.springframework.stereotype.Service;

/**
 * @author 黄磊
 */
@Service
public class SysUserManagerImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserManager {
}
