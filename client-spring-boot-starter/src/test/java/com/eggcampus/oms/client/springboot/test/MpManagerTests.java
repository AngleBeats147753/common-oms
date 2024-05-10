package com.eggcampus.oms.client.springboot.test;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.eggcampus.oms.client.springboot.EnableOms;
import com.eggcampus.oms.client.springboot.OmsManager;
import com.eggcampus.oms.client.springboot.test.dao.UserMapper;
import com.eggcampus.oms.client.springboot.test.manager.LogicDeletionUserManager;
import com.eggcampus.oms.client.springboot.test.manager.UserManager;
import com.eggcampus.oms.client.springboot.test.pojo.User;
import com.eggcampus.util.test.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

/**
 * @author 黄磊
 */
@EnableOms
@SpringBootTest(classes = TestMain.class)
public class MpManagerTests {
    private static final Path baseDir = Paths.get("MpManager");
    @Autowired
    private DataSource dataSource;
    @Autowired
    private UserManager userManager;
    @Autowired
    private LogicDeletionUserManager logicDeletionUserManager;
    @MockBean
    private OmsManager omsManager;

    @BeforeEach
    void beforeEach() throws SQLException, IOException {
        TestUtil.initTable(dataSource, Path.of("schema.sql"));
    }

    @Transactional
    @ParameterizedTest(name = "{1}")
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
            caseName, description,          exception
            case1   , 测试updateById是否自动管理              , null
            """)
    public void test_updateById(ArgumentsAccessor args) throws Exception {
        HashMap<String, Object> map = prepare_updateById(args);
        execute_updateById(map.get("inputParam"), map.get("exception"));
        compare_updateById(map.get("gt"));
    }

    public HashMap<String, Object> prepare_updateById(ArgumentsAccessor args) throws Exception {
        Path caseDir = baseDir.resolve("updateById").resolve(args.getString(0));
        // 初始化数据库数据
        TestUtil.initData(caseDir, dataSource);
        // 准备输入参数
        JSONObject param = TestUtil.getInputParam(caseDir);
        User user = ObjectUtil.clone(userManager.getById(1L));
        user.setImages(param.getStr("newImages"));
        // 准备gt
        JSONObject gt = TestUtil.readJsonObject(caseDir.resolve("gt.json"));
        // 准备异常
        Class<?> exception = TestUtil.getException(args.getString(2));

        // mock
        doNothing().when(omsManager).change(any(String.class), any(String.class));

        HashMap<String, Object> result = new HashMap<>();
        result.put("inputParam", new Object[]{user});
        result.put("exception", exception);
        result.put("gt", gt);
        return result;
    }

    public void execute_updateById(Object paramObject, Object exceptionObject) {
        TestUtil.execute(paramObject, exceptionObject, (param -> {
            userManager.updateById((User) param[0]);
        }));
    }

    public void compare_updateById(Object gt) {
        JSONObject gtObject = (JSONObject) gt;
        verify(omsManager).change(gtObject.getStr("oldUrls"), gtObject.getStr("newUrls"));
    }

    @Transactional
    @ParameterizedTest(name = "{1}")
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
            caseName, description,          exception
            case1   , 测试save是否自动管理资源   , null
            """)
    public void test_save(ArgumentsAccessor args) throws Exception {
        HashMap<String, Object> map = prepare_save(args);
        execute_save(map.get("inputParam"), map.get("exception"));
        compare_save(map.get("gt"));
    }

    public HashMap<String, Object> prepare_save(ArgumentsAccessor args) throws Exception {
        Path caseDir = baseDir.resolve("save").resolve(args.getString(0));
        // 准备输入参数
        User param = TestUtil.getInputParam(caseDir, User.class);
        // 准备gt
        JSONObject gt = TestUtil.getGt(caseDir);
        // 准备异常
        Class<?> exception = TestUtil.getException(args.getString(2));

        // mock
        doNothing().when(omsManager).use(any(String.class));

        HashMap<String, Object> result = new HashMap<>();
        result.put("inputParam", new Object[]{param});
        result.put("exception", exception);
        result.put("gt", gt);
        return result;
    }

    public void execute_save(Object paramObject, Object exceptionObject) {
        TestUtil.execute(paramObject, exceptionObject, (param -> {
            userManager.save((User) param[0]);
        }));
    }

    public void compare_save(Object gt) {
        JSONObject gtObject = (JSONObject) gt;
        verify(omsManager).use(gtObject.getStr("newUrls"));
    }

    @Transactional
    @ParameterizedTest(name = "{1}")
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
            caseName, description,          exception
            case1   , 测试removeById是否自动管理资源              , null
            """)
    public void test_removeById(ArgumentsAccessor args) throws Exception {
        HashMap<String, Object> map = prepare_removeById(args);
        execute_removeById(map.get("inputParam"), map.get("exception"));
        compare_removeById(map.get("gt"));
    }

    public HashMap<String, Object> prepare_removeById(ArgumentsAccessor args) throws Exception {
        Path caseDir = baseDir.resolve("removeById").resolve(args.getString(0));
        // 初始化数据库数据
        TestUtil.initData(caseDir, dataSource);
        // 准备输入参数
        JSONObject param = TestUtil.getInputParam(caseDir);
        // 准备gt
        JSONObject gt = TestUtil.getGt(caseDir);
        // 准备异常
        Class<?> exception = TestUtil.getException(args.getString(2));

        // mock
        doNothing().when(omsManager).delete(any(String.class));

        HashMap<String, Object> result = new HashMap<>();
        result.put("inputParam", new Object[]{param.getLong("id")});
        result.put("exception", exception);
        result.put("gt", gt);
        return result;
    }

    public void execute_removeById(Object paramObject, Object exceptionObject) {
        TestUtil.execute(paramObject, exceptionObject, (param -> {
            userManager.removeById((Long) param[0]);
        }));
    }

    public void compare_removeById(Object gt) {
        JSONObject gtObject = (JSONObject) gt;
        verify(omsManager).delete(gtObject.getStr("oldUrls"));
    }

    @Transactional
    @ParameterizedTest(name = "{1}")
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
            caseName, description,          exception
            case1   , 测试逻辑删除的removeById是否自动管理资源              , null
            """)
    public void test_removeByIdLogically(ArgumentsAccessor args) throws Exception {
        HashMap<String, Object> map = prepare_removeByIdLogically(args);
        execute_removeByIdLogically(map.get("inputParam"), map.get("exception"));
        compare_removeByIdLogically(map.get("gt"));
    }

    public HashMap<String, Object> prepare_removeByIdLogically(ArgumentsAccessor args) throws Exception {
        Path caseDir = baseDir.resolve("removeById").resolve(args.getString(0));
        // 初始化数据库数据
        TestUtil.initData(caseDir, dataSource);
        // 准备输入参数
        JSONObject param = TestUtil.getInputParam(caseDir);
        // 准备gt
        JSONObject gt = TestUtil.getGt(caseDir);
        // 准备异常
        Class<?> exception = TestUtil.getException(args.getString(2));

        // mock
        doNothing().when(omsManager).delete(any(String.class));

        HashMap<String, Object> result = new HashMap<>();
        result.put("inputParam", new Object[]{param.getLong("id")});
        result.put("exception", exception);
        result.put("gt", gt);
        return result;
    }

    public void execute_removeByIdLogically(Object paramObject, Object exceptionObject) {
        TestUtil.execute(paramObject, exceptionObject, (param -> {
            logicDeletionUserManager.removeById((Long) param[0]);
        }));
    }

    public void compare_removeByIdLogically(Object gt) {
        JSONObject gtObject = (JSONObject) gt;
        verify(omsManager).delete(gtObject.getStr("oldUrls"));
    }
}
