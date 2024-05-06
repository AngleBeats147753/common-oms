package com.eggcampus.oms.client.springboot.mybatis;

import com.eggcampus.oms.client.springboot.OmsUtil;
import com.eggcampus.oms.client.springboot.TestMain;
import com.eggcampus.oms.client.springboot.mybatis.dao.SysUserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 黄磊
 */
@Import(MybatisConfig.class)
@SpringBootTest(classes = TestMain.class)
public class MybatisTests {
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysUserManager sysUserManager;

    @Transactional
    @Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        SysUser sysUser = sysUserMapper.selectById(2);
        sysUser.setName(OmsUtil.convert2Str(List.of("https://test.img.eggcampus.com/hl-dev/1-aaa5579", "https://test.img.eggcampus.com/hl-dev/1-aaa5580")));
        sysUserManager.updateById(sysUser);
//        sysUserManager.removeById(2);
    }
}
