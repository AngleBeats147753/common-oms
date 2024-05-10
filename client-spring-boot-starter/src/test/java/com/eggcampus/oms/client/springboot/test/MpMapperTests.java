package com.eggcampus.oms.client.springboot.test;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.eggcampus.oms.client.springboot.EnableOms;
import com.eggcampus.oms.client.springboot.OmsManager;
import com.eggcampus.oms.client.springboot.test.dao.LogicDeletionUserMapper;
import com.eggcampus.oms.client.springboot.test.dao.UserMapper;
import com.eggcampus.oms.client.springboot.test.pojo.LogicDeletionUser;
import com.eggcampus.oms.client.springboot.test.pojo.User;
import com.eggcampus.util.test.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
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
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

/**
 * @author 黄磊
 */
@SuppressWarnings("unchecked")
@EnableOms
@SpringBootTest(classes = TestMain.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class MpMapperTests {
    private static final Path baseDir = Paths.get("MpMapper");
    @Autowired
    private DataSource dataSource;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private LogicDeletionUserMapper logicDeletionUserMapper;
    @MockBean
    private OmsManager omsManager;

    @BeforeEach
    void beforeEach() throws SQLException, IOException {
        TestUtil.initTable(dataSource, Path.of("schema.sql"));
    }

    @Transactional
    @DisplayName("测试oms拦截insert(T entity)")
    @ParameterizedTest(name = "{1}")
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
            caseName, description,          exception
            case1   , 基本        , null
            """)
    public void test_insert(ArgumentsAccessor args) throws Exception {
        HashMap<String, Object> map = prepare_insert(args);
        execute_insert(map.get("inputParam"), map.get("exception"));
        compare_insert(map.get("gt"));
    }

    public HashMap<String, Object> prepare_insert(ArgumentsAccessor args) throws Exception {
        Path caseDir = baseDir.resolve("insert").resolve(args.getString(0));
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

    public void execute_insert(Object paramObject, Object exceptionObject) {
        TestUtil.execute(paramObject, exceptionObject, (param -> {
            userMapper.insert((User) param[0]);
        }));
    }

    public void compare_insert(Object gt) {
        JSONObject gtObject = (JSONObject) gt;
        verify(omsManager).use(gtObject.getStr("newUrls"));
    }

    @Transactional
    @DisplayName("测试oms拦截deleteById(Serializable id)")
    @ParameterizedTest(name = "{1}")
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
            caseName, description, exception
            case1   , 基本        , null
            """)
    public void test_deleteById(ArgumentsAccessor args) throws Exception {
        HashMap<String, Object> map = prepare_deleteById(args);
        execute_deleteById(map.get("inputParam"), map.get("exception"));
        compare_deleteById(map.get("gt"));
    }

    public HashMap<String, Object> prepare_deleteById(ArgumentsAccessor args) throws Exception {
        Path caseDir = baseDir.resolve("deleteById").resolve(args.getString(0));
        // 初始化数据库数据
        TestUtil.initData(caseDir, dataSource);
        // 准备输入参数
        JSONObject param = TestUtil.getInputParam(caseDir);
        // 准备gt
        List<String> gt = TestUtil.getGts(caseDir, String.class);
        // 准备异常
        Class<?> exception = TestUtil.getException(args.getString(2));

        // mock
        doNothing().when(omsManager).delete(any(List.class));

        HashMap<String, Object> result = new HashMap<>();
        result.put("inputParam", new Object[]{param.getLong("id")});
        result.put("exception", exception);
        result.put("gt", gt);
        return result;
    }

    public void execute_deleteById(Object paramObject, Object exceptionObject) {
        TestUtil.execute(paramObject, exceptionObject, (param -> {
            userMapper.deleteById((Long) param[0]);
        }));
    }

    public void compare_deleteById(Object gt) {
        List<String> gtObject = (List<String>) gt;
        verify(omsManager).delete(gtObject);
    }

    @Transactional
    @DisplayName("测试oms拦截deleteById(T entity)")
    @ParameterizedTest(name = "{1}")
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
            caseName, description,      exception
            case1   , 基本              , null
            """)
    public void test_deleteById2(ArgumentsAccessor args) throws Exception {
        HashMap<String, Object> map = prepare_deleteById2(args);
        execute_deleteById2(map.get("inputParam"), map.get("exception"));
        compare_deleteById2(map.get("gt"));
    }

    public HashMap<String, Object> prepare_deleteById2(ArgumentsAccessor args) throws Exception {
        Path caseDir = baseDir.resolve("deleteById2").resolve(args.getString(0));
        // 初始化数据库数据
        TestUtil.initData(caseDir, dataSource);
        // 准备输入参数
        JSONObject param = TestUtil.getInputParam(caseDir);
        User user = ObjectUtil.clone(userMapper.selectById(param.getLong("id")));
        // 准备gt
        List<String> gt = TestUtil.getGts(caseDir, String.class);
        // 准备异常
        Class<?> exception = TestUtil.getException(args.getString(2));

        // mock
        doNothing().when(omsManager).delete(any(List.class));

        HashMap<String, Object> result = new HashMap<>();
        result.put("inputParam", new Object[]{user});
        result.put("exception", exception);
        result.put("gt", gt);
        return result;
    }

    public void execute_deleteById2(Object paramObject, Object exceptionObject) {
        TestUtil.execute(paramObject, exceptionObject, (param -> {
            userMapper.deleteById((User) param[0]);
        }));
    }

    public void compare_deleteById2(Object gt) {
        List<String> gtObject = (List<String>) gt;
        verify(omsManager).delete(gtObject);
    }

    @Transactional
    @DisplayName("测试oms拦截deleteBatchIds(Collection<? extends Serializable> ids)")
    @ParameterizedTest(name = "{1}")
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
            caseName, description,          exception
            case1   , 基本              , null
            """)
    public void test_deleteBatchIds(ArgumentsAccessor args) throws Exception {
        HashMap<String, Object> map = prepare_deleteBatchIds(args);
        execute_deleteBatchIds(map.get("inputParam"), map.get("exception"));
        compare_deleteBatchIds(map.get("gt"));
    }

    public HashMap<String, Object> prepare_deleteBatchIds(ArgumentsAccessor args) throws Exception {
        Path caseDir = baseDir.resolve("deleteBatchIds").resolve(args.getString(0));
        // 初始化数据库数据
        TestUtil.initData(caseDir, dataSource);
        // 准备输入参数
        List<Long> param = TestUtil.getInputParams(caseDir, Long.class);
        // 准备gt
        List<String> gt = TestUtil.getGts(caseDir, String.class);
        // 准备异常
        Class<?> exception = TestUtil.getException(args.getString(2));
        // mock
        doNothing().when(omsManager).delete(any(List.class));

        HashMap<String, Object> result = new HashMap<>();
        result.put("inputParam", new Object[]{param});
        result.put("exception", exception);
        result.put("gt", gt);
        return result;
    }

    @SuppressWarnings("unchecked")
    public void execute_deleteBatchIds(Object paramObject, Object exceptionObject) {
        TestUtil.execute(paramObject, exceptionObject, (param -> {
            userMapper.deleteBatchIds((List<Long>) param[0]);
        }));
    }

    public void compare_deleteBatchIds(Object gt) {
        List<String> gtObject = (List<String>) gt;
        verify(omsManager).delete(gtObject);
    }

    @Transactional
    @DisplayName("测试oms拦截delete(Wrapper<T> queryWrapper)")
    @ParameterizedTest(name = "{1}")
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
            caseName, description,          exception
            case1   , 基本              , null
            """)
    public void test_delete(ArgumentsAccessor args) throws Exception {
        HashMap<String, Object> map = prepare_delete(args);
        execute_delete(map.get("inputParam"), map.get("exception"));
        compare_delete(map.get("gt"));
    }

    public HashMap<String, Object> prepare_delete(ArgumentsAccessor args) throws Exception {
        Path caseDir = baseDir.resolve("delete").resolve(args.getString(0));
        // 初始化数据库数据
        TestUtil.initData(caseDir, dataSource);
        // 准备输入参数
        List<Long> param = TestUtil.getInputParams(caseDir, Long.class);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.in("id", param);
        // 准备gt
        List<String> gt = TestUtil.getGts(caseDir, String.class);
        // 准备异常
        Class<?> exception = TestUtil.getException(args.getString(2));
        // mock
        doNothing().when(omsManager).delete(any(List.class));

        HashMap<String, Object> result = new HashMap<>();
        result.put("inputParam", new Object[]{wrapper});
        result.put("exception", exception);
        result.put("gt", gt);
        return result;
    }

    @SuppressWarnings("unchecked")
    public void execute_delete(Object paramObject, Object exceptionObject) {
        TestUtil.execute(paramObject, exceptionObject, (param -> {
            userMapper.delete((Wrapper<User>) param[0]);
        }));
    }

    public void compare_delete(Object gt) {
        List<String> gtObject = (List<String>) gt;
        verify(omsManager).delete(gtObject);
    }

    @Transactional
    @DisplayName("测试oms拦截deleteByMap(Map<String, Object> columnMap)")
    @ParameterizedTest(name = "{1}")
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
            caseName, description,          exception
            case1   , 基本              , null
            """)
    public void test_deleteByMap(ArgumentsAccessor args) throws Exception {
        HashMap<String, Object> map = prepare_deleteByMap(args);
        execute_deleteByMap(map.get("inputParam"), map.get("exception"));
        compare_deleteByMap(map.get("gt"));
    }

    public HashMap<String, Object> prepare_deleteByMap(ArgumentsAccessor args) throws Exception {
        Path caseDir = baseDir.resolve("deleteByMap").resolve(args.getString(0));
        // 初始化数据库数据
        TestUtil.initData(caseDir, dataSource);
        // 准备输入参数
        JSONObject param = TestUtil.getInputParam(caseDir);
        Map<String, Object> map = Map.of("age", param.get("age"));
        // 准备gt
        List<String> gt = TestUtil.getGts(caseDir, String.class);
        // 准备异常
        Class<?> exception = TestUtil.getException(args.getString(2));
        // mock
        doNothing().when(omsManager).delete(any(List.class));

        HashMap<String, Object> result = new HashMap<>();
        result.put("inputParam", new Object[]{map});
        result.put("exception", exception);
        result.put("gt", gt);
        return result;
    }

    @SuppressWarnings("unchecked")
    public void execute_deleteByMap(Object paramObject, Object exceptionObject) {
        TestUtil.execute(paramObject, exceptionObject, (param -> {
            userMapper.deleteByMap((Map<String, Object>) param[0]);
        }));
    }

    public void compare_deleteByMap(Object gt) {
        List<String> gtObject = (List<String>) gt;
        verify(omsManager).delete(gtObject);
    }

    @Transactional
    @DisplayName("测试oms拦截逻辑删除的deleteById(Serializable id)")
    @ParameterizedTest(name = "{1}")
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
            caseName, description,          exception
            case1   , 基本              , null
            """)
    public void test_deleteByIdLogically(ArgumentsAccessor args) throws Exception {
        HashMap<String, Object> map = prepare_deleteByIdLogically(args);
        execute_deleteByIdLogically(map.get("inputParam"), map.get("exception"));
        compare_deleteByIdLogically(map.get("gt"));
    }

    public HashMap<String, Object> prepare_deleteByIdLogically(ArgumentsAccessor args) throws Exception {
        Path caseDir = baseDir.resolve("deleteById").resolve(args.getString(0));
        // 初始化数据库数据
        TestUtil.initData(caseDir, dataSource);
        // 准备输入参数
        JSONObject param = TestUtil.getInputParam(caseDir);
        // 准备gt
        List<String> gt = TestUtil.getGts(caseDir, String.class);
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

    public void execute_deleteByIdLogically(Object paramObject, Object exceptionObject) {
        TestUtil.execute(paramObject, exceptionObject, (param -> {
            logicDeletionUserMapper.deleteById((Long) param[0]);
        }));
    }

    public void compare_deleteByIdLogically(Object gt) {
        List<String> gtObject = (List<String>) gt;
        verify(omsManager).delete(gtObject);
    }

    @Transactional
    @DisplayName("测试oms拦截逻辑删除的deleteById(T entity)")
    @ParameterizedTest(name = "{1}")
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
            caseName, description,      exception
            case1   , 基本              , null
            """)
    public void test_deleteByIdLogically2(ArgumentsAccessor args) throws Exception {
        HashMap<String, Object> map = prepare_deleteByIdLogically2(args);
        execute_deleteByIdLogically2(map.get("inputParam"), map.get("exception"));
        compare_deleteByIdLogically2(map.get("gt"));
    }

    public HashMap<String, Object> prepare_deleteByIdLogically2(ArgumentsAccessor args) throws Exception {
        Path caseDir = baseDir.resolve("deleteByIdLogically2").resolve(args.getString(0));
        // 初始化数据库数据
        TestUtil.initData(caseDir, dataSource);
        // 准备输入参数
        JSONObject param = TestUtil.getInputParam(caseDir);
        LogicDeletionUser user = ObjectUtil.clone(logicDeletionUserMapper.selectById(param.getLong("id")));
        // 准备gt
        List<String> gt = TestUtil.getGts(caseDir, String.class);
        // 准备异常
        Class<?> exception = TestUtil.getException(args.getString(2));

        // mock
        doNothing().when(omsManager).delete(any(List.class));

        HashMap<String, Object> result = new HashMap<>();
        result.put("inputParam", new Object[]{user});
        result.put("exception", exception);
        result.put("gt", gt);
        return result;
    }

    public void execute_deleteByIdLogically2(Object paramObject, Object exceptionObject) {
        TestUtil.execute(paramObject, exceptionObject, (param -> {
            logicDeletionUserMapper.deleteById((LogicDeletionUser) param[0]);
        }));
    }

    public void compare_deleteByIdLogically2(Object gt) {
        List<String> gtObject = (List<String>) gt;
        verify(omsManager).delete(gtObject);
    }

    @Transactional
    @DisplayName("测试oms拦截逻辑删除的deleteBatchIds(Collection<? extends Serializable> ids)")
    @ParameterizedTest(name = "{1}")
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
            caseName, description,          exception
            case1   , 基本              , null
            """)
    public void test_deleteBatchIdsLogically(ArgumentsAccessor args) throws Exception {
        HashMap<String, Object> map = prepare_deleteBatchIdsLogically(args);
        execute_deleteBatchIdsLogically(map.get("inputParam"), map.get("exception"));
        compare_deleteBatchIdsLogically(map.get("gt"));
    }

    public HashMap<String, Object> prepare_deleteBatchIdsLogically(ArgumentsAccessor args) throws Exception {
        Path caseDir = baseDir.resolve("deleteBatchIds").resolve(args.getString(0));
        // 初始化数据库数据
        TestUtil.initData(caseDir, dataSource);
        // 准备输入参数
        List<Long> param = TestUtil.getInputParams(caseDir, Long.class);
        // 准备gt
        List<String> gt = TestUtil.getGts(caseDir, String.class);
        // 准备异常
        Class<?> exception = TestUtil.getException(args.getString(2));
        // mock
        doNothing().when(omsManager).delete(any(List.class));

        HashMap<String, Object> result = new HashMap<>();
        result.put("inputParam", new Object[]{param});
        result.put("exception", exception);
        result.put("gt", gt);
        return result;
    }

    @SuppressWarnings("unchecked")
    public void execute_deleteBatchIdsLogically(Object paramObject, Object exceptionObject) {
        TestUtil.execute(paramObject, exceptionObject, (param -> {
            logicDeletionUserMapper.deleteBatchIds((List<Long>) param[0]);
        }));
    }

    public void compare_deleteBatchIdsLogically(Object gt) {
        List<String> gtObject = (List<String>) gt;
        verify(omsManager).delete(gtObject);
    }

    @Transactional
    @DisplayName("测试oms拦截逻辑删除的delete(Wrapper<T> queryWrapper)")
    @ParameterizedTest(name = "{1}")
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
            caseName, description,          exception
            case1   , 基本              , null
            """)
    public void test_deleteLogically(ArgumentsAccessor args) throws Exception {
        HashMap<String, Object> map = prepare_deleteLogically(args);
        execute_deleteLogically(map.get("inputParam"), map.get("exception"));
        compare_deleteLogically(map.get("gt"));
    }

    public HashMap<String, Object> prepare_deleteLogically(ArgumentsAccessor args) throws Exception {
        Path caseDir = baseDir.resolve("delete").resolve(args.getString(0));
        // 初始化数据库数据
        TestUtil.initData(caseDir, dataSource);
        // 准备输入参数
        List<Long> param = TestUtil.getInputParams(caseDir, Long.class);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.in("id", param);
        // 准备gt
        List<String> gt = TestUtil.getGts(caseDir, String.class);
        // 准备异常
        Class<?> exception = TestUtil.getException(args.getString(2));
        // mock
        doNothing().when(omsManager).delete(any(List.class));

        HashMap<String, Object> result = new HashMap<>();
        result.put("inputParam", new Object[]{wrapper});
        result.put("exception", exception);
        result.put("gt", gt);
        return result;
    }

    @SuppressWarnings("unchecked")
    public void execute_deleteLogically(Object paramObject, Object exceptionObject) {
        TestUtil.execute(paramObject, exceptionObject, (param -> {
            logicDeletionUserMapper.delete((Wrapper<LogicDeletionUser>) param[0]);
        }));
    }

    public void compare_deleteLogically(Object gt) {
        List<String> gtObject = (List<String>) gt;
        verify(omsManager).delete(gtObject);
    }

    @Transactional
    @DisplayName("测试oms拦截逻辑删除的deleteByMap(Map<String, Object> columnMap)")
    @ParameterizedTest(name = "{1}")
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
            caseName, description,          exception
            case1   , 基本              , null
            """)
    public void test_deleteByMapLogically(ArgumentsAccessor args) throws Exception {
        HashMap<String, Object> map = prepare_deleteByMapLogically(args);
        execute_deleteByMapLogically(map.get("inputParam"), map.get("exception"));
        compare_deleteByMapLogically(map.get("gt"));
    }

    public HashMap<String, Object> prepare_deleteByMapLogically(ArgumentsAccessor args) throws Exception {
        Path caseDir = baseDir.resolve("deleteByMap").resolve(args.getString(0));
        // 初始化数据库数据
        TestUtil.initData(caseDir, dataSource);
        // 准备输入参数
        JSONObject param = TestUtil.getInputParam(caseDir);
        Map<String, Object> map = Map.of("age", param.get("age"));
        // 准备gt
        List<String> gt = TestUtil.getGts(caseDir, String.class);
        // 准备异常
        Class<?> exception = TestUtil.getException(args.getString(2));
        // mock
        doNothing().when(omsManager).delete(any(List.class));

        HashMap<String, Object> result = new HashMap<>();
        result.put("inputParam", new Object[]{map});
        result.put("exception", exception);
        result.put("gt", gt);
        return result;
    }

    @SuppressWarnings("unchecked")
    public void execute_deleteByMapLogically(Object paramObject, Object exceptionObject) {
        TestUtil.execute(paramObject, exceptionObject, (param -> {
            logicDeletionUserMapper.deleteByMap((Map<String, Object>) param[0]);
        }));
    }

    public void compare_deleteByMapLogically(Object gt) {
        List<String> gtObject = (List<String>) gt;
        verify(omsManager).delete(gtObject);
    }


    @Transactional
    @DisplayName("测试oms拦截updateById(T entity)")
    @ParameterizedTest(name = "{1}")
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
            caseName, description,          exception
            case1   , 基本              , null
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
        User user = ObjectUtil.clone(userMapper.selectById(1L));
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
            userMapper.updateById((User) param[0]);
        }));
    }

    public void compare_updateById(Object gt) {
        JSONObject gtObject = (JSONObject) gt;
        verify(omsManager).change(gtObject.getBeanList("oldUrls", String.class), gtObject.getBeanList("newUrls", String.class));
    }

    @Transactional
    @DisplayName("测试oms拦截update(T entity, Wrapper<T> updateWrapper)")
    @ParameterizedTest(name = "{1}")
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
            caseName, description                                                  ,          exception
            case1   , 基本      , null
            """)
    public void test_update(ArgumentsAccessor args) throws Exception {
        HashMap<String, Object> map = prepare_update(args);
        execute_update(map.get("inputParam"), map.get("exception"));
        compare_update(map.get("gt"));
    }

    public HashMap<String, Object> prepare_update(ArgumentsAccessor args) throws Exception {
        Path caseDir = baseDir.resolve("update").resolve(args.getString(0));
        // 初始化数据库数据
        TestUtil.initData(caseDir, dataSource);
        // 准备输入参数
        JSONObject param = TestUtil.getInputParam(caseDir);
        User user = ObjectUtil.clone(userMapper.selectById(1L));
        user.setImages(param.getStr("newImages"));
        QueryWrapper<Object> wrapper = new QueryWrapper<>();
        wrapper.eq("age", param.getInt("age"));
        // 准备gt
        JSONObject gt = TestUtil.readJsonObject(caseDir.resolve("gt.json"));
        // 准备异常
        Class<?> exception = TestUtil.getException(args.getString(2));

        // mock
        doNothing().when(omsManager).change(any(String.class), any(String.class));

        HashMap<String, Object> result = new HashMap<>();
        result.put("inputParam", new Object[]{user, wrapper});
        result.put("exception", exception);
        result.put("gt", gt);
        return result;
    }

    public void execute_update(Object paramObject, Object exceptionObject) {
        TestUtil.execute(paramObject, exceptionObject, (param -> {
            userMapper.update((User) param[0], (QueryWrapper<User>) param[1]);
        }));
    }

    public void compare_update(Object gt) {
        JSONObject gtObject = (JSONObject) gt;
        verify(omsManager).change(gtObject.getBeanList("oldUrls", String.class), gtObject.getBeanList("newUrls", String.class));
    }

    @Transactional
    @DisplayName("测试oms拦截update(Wrapper<T> updateWrapper)")
    @ParameterizedTest(name = "{1}")
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
            caseName, description                                     ,   exception
            case1   , 基本   , null
            """)
    public void test_update2(ArgumentsAccessor args) throws Exception {
        HashMap<String, Object> map = prepare_update2(args);
        execute_update2(map.get("inputParam"), map.get("exception"));
        compare_update2(map.get("gt"));
    }

    public HashMap<String, Object> prepare_update2(ArgumentsAccessor args) throws Exception {
        Path caseDir = baseDir.resolve("update").resolve(args.getString(0));
        // 初始化数据库数据
        TestUtil.initData(caseDir, dataSource);
        // 准备输入参数
        JSONObject param = TestUtil.getInputParam(caseDir);
        UpdateWrapper<Object> wrapper = new UpdateWrapper<>();
        wrapper.eq("age", param.getInt("age"));
        wrapper.set("images", param.getStr("newImages"));
        wrapper.set("age", 21);
        // 准备gt
        JSONObject gt = TestUtil.readJsonObject(caseDir.resolve("gt.json"));
        // 准备异常
        Class<?> exception = TestUtil.getException(args.getString(2));

        // mock
        doNothing().when(omsManager).change(any(String.class), any(String.class));

        HashMap<String, Object> result = new HashMap<>();
        result.put("inputParam", new Object[]{wrapper});
        result.put("exception", exception);
        result.put("gt", gt);
        return result;
    }

    public void execute_update2(Object paramObject, Object exceptionObject) {
        TestUtil.execute(paramObject, exceptionObject, (param -> {
            userMapper.update((UpdateWrapper<User>) param[0]);
        }));
    }

    public void compare_update2(Object gt) {
        JSONObject gtObject = (JSONObject) gt;
        verify(omsManager).change(gtObject.getBeanList("oldUrls", String.class), gtObject.getBeanList("newUrls", String.class));
    }
}
