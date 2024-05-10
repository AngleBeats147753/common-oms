package com.eggcampus.oms.client.springboot;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.eggcampus.util.base.ReflectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 黄磊
 */
@SuppressWarnings("unchecked")
@Slf4j
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
})
public class OmsInterceptor implements Interceptor {
    private final OmsManager omsManager;
    private final ThreadLocal<Boolean> proceedFlag = new ThreadLocal<>();
    private final ThreadLocal<Object> proceedResult = new ThreadLocal<>();

    public OmsInterceptor(OmsManager omsManager) {
        this.omsManager = omsManager;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        proceedFlag.set(false);

        processOms(invocation);

        Object result;
        if (proceedFlag.get()) {
            result = proceedResult.get();
        } else {
            result = invocation.proceed();
        }
        proceedFlag.remove();
        proceedResult.remove();
        return result;
    }

    private void processOms(Invocation invocation) throws InvocationTargetException, IllegalAccessException {
        Executor executor = (Executor) invocation.getTarget();
        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];

        String statementId = getStatementId(ms);
        try {
            if (SqlCommandType.INSERT == ms.getSqlCommandType()) {
                if ("insert".equals(statementId)) {
                    insert(parameter);
                }
            } else if (SqlCommandType.DELETE.equals(ms.getSqlCommandType())
                    || SqlCommandType.UPDATE.equals(ms.getSqlCommandType()) && ms.getId().contains("delete")) {
                switch (statementId) {
                    case "deleteById" -> deleteBydId(executor, ms, parameter);
                    case "deleteBatchIds" -> batchDelete(executor, ms, parameter, "selectBatchIds");
                    case "delete" -> batchDelete(executor, ms, parameter, "selectList");
                    case "deleteByMap" -> batchDelete(executor, ms, parameter, "selectByMap");
                }
            } else if (SqlCommandType.UPDATE.equals(ms.getSqlCommandType())) {
                switch (statementId) {
                    case "updateById" -> updateById(executor, ms, parameter);
                    case "update" -> batchUpdate(invocation, ms, parameter);
                }
            }
        } catch (NotProcessException e) {
            log.debug("不处理oms", e);
        }
    }

    private void insert(Object et) {
        List<Field> fields = ReflectUtil.getFieldByAnnotation(et.getClass(), OmsResource.class);
        if (fields.isEmpty()) {
            return;
        }
        for (Field field : fields) {
            try {
                String resources = (String) field.get(et);
                omsManager.use(resources);
            } catch (IllegalAccessException e) {
                throw new NotProcessException("获取实体的资源失败", e);
            }
        }
    }

    private void deleteBydId(Executor executor, MappedStatement ms, Object parameter) {
        boolean isEntity = getEntityId(parameter) != null;
        ArrayList<String> oldResources;
        if (isEntity) {
            oldResources = getResource(parameter);
        } else {
            Object oldEntity = selectById(executor, ms, parameter);
            oldResources = getResource(oldEntity);
        }
        omsManager.delete(oldResources);
    }

    private void batchDelete(Executor executor, MappedStatement ms, Object parameter, String selectMethod) {
        ArrayList<String> oldResources = new ArrayList<>();
        try {
            MappedStatement select = getTargetStatement(ms, selectMethod);
            executor.query(select, parameter, RowBounds.DEFAULT, resultContext -> {
                try {
                    Object entity = resultContext.getResultObject();
                    List<Field> fields = ReflectUtil.getFieldByAnnotation(entity.getClass(), OmsResource.class);
                    if (fields.isEmpty()) {
                        return;
                    }
                    for (Field field : fields) {
                        String resources = (String) field.get(entity);
                        oldResources.addAll(OmsUtil.convert2List(resources));
                    }
                } catch (IllegalAccessException e) {
                    throw new OmsException("获取资源失败", e);
                }
            });
            omsManager.delete(oldResources);
        } catch (SQLException e) {
            throw new OmsException("查询资源失败", e);
        }
    }

    private void updateById(Executor executor, MappedStatement ms, Object parameter) {
        Object newEntity = getEntity(parameter);
        ArrayList<String> newResources = getResource(newEntity);
        Object id = findEntityId(newEntity);
        Object oldEntity = selectById(executor, ms, id);
        ArrayList<String> oldResources = getResource(oldEntity);
        omsManager.change(oldResources, newResources);
    }

    private void batchUpdate(Invocation invocation, MappedStatement ms, Object parameter) throws InvocationTargetException, IllegalAccessException {
        Executor executor = (Executor) invocation.getTarget();
        Wrapper<?> wrapper = getWrapper(parameter);
        ArrayList<String> oldResources = new ArrayList<>();
        ArrayList<String> newResources = new ArrayList<>();

        if (wrapper instanceof UpdateWrapper<?>) {
            MappedStatement beforeProceedSql = getTargetStatement(ms, "selectList");
            MapperMethod.ParamMap<Object> beforeProceedParam = new MapperMethod.ParamMap<>();
            beforeProceedParam.put("param1", wrapper);
            beforeProceedParam.put(Constants.WRAPPER, wrapper);

            MappedStatement afterProceedSql = getTargetStatement(ms, "selectById");
            HashMap<String, Object> map = new HashMap<>();
            map.put("count", 0);
            try {
                executor.query(beforeProceedSql, beforeProceedParam, RowBounds.DEFAULT, resultContext -> {
                    Object oldEntity = resultContext.getResultObject();
                    oldResources.addAll(getResource(oldEntity));
                    map.computeIfAbsent("id", key -> findEntityId(oldEntity));
                    map.put("count", (int) map.get("count") + 1);
                });
                Object result = invocation.proceed();
                proceedFlag.set(true);
                proceedResult.set(result);
                if (map.get("id") == null) {
                    return;
                }
                executor.query(afterProceedSql, map.get("id"), RowBounds.DEFAULT, resultContext -> {
                    Object newEntity = resultContext.getResultObject();
                    ArrayList<String> tempResource = getResource(newEntity);
                    for (int i = 0; i < (int) map.get("count"); i++) {
                        newResources.addAll(tempResource);
                    }
                });
            } catch (SQLException e) {
                throw new OmsException("查询资源失败", e);
            }
        } else {
            Object newEntity = getEntity(parameter);
            ArrayList<String> tempNewResource = getResource(newEntity);
            try {
                MappedStatement select = getTargetStatement(ms, "selectList");
                MapperMethod.ParamMap<Object> selectMap = new MapperMethod.ParamMap<>();
                selectMap.put("param1", wrapper);
                selectMap.put(Constants.WRAPPER, wrapper);
                executor.query(select, selectMap, RowBounds.DEFAULT, resultContext -> {
                    Object oldEntity = resultContext.getResultObject();
                    oldResources.addAll(getResource(oldEntity));
                    newResources.addAll(tempNewResource);
                });
            } catch (SQLException e) {
                throw new OmsException("查询资源失败", e);
            }
        }
        omsManager.change(oldResources, newResources);
    }

    /**
     * 根据id查询实体
     */
    private Object selectById(Executor executor, MappedStatement ms, Object id) {
        String[] splits = ms.getId().split("\\.");
        splits[splits.length - 1] = "selectById";
        String selectSqlId = String.join(".", splits);
        MappedStatement selectMappedStatement = ms.getConfiguration().getMappedStatement(selectSqlId);
        try {
            List<Object> query = executor.query(selectMappedStatement, id, RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER);
            return query.get(0);
        } catch (SQLException e) {
            throw new NotProcessException("获取数据库中实体失败", e);
        }
    }

    private Object getEntity(Object parameter) {
        Map<String, Object> map = (Map<String, Object>) parameter;
        Object et = map.getOrDefault(Constants.ENTITY, null);
        if (et == null) {
            throw new NotProcessException("没有找到entity参数");
        }
        return et;
    }

    private Wrapper<?> getWrapper(Object parameter) {
        Map<String, Object> map = (Map<String, Object>) parameter;
        Object wrapper = map.getOrDefault(Constants.WRAPPER, null);
        if (wrapper == null) {
            throw new NotProcessException("没有找到wrapper参数");
        }
        return (Wrapper<?>) wrapper;
    }

    private Object getEntityId(Object et) {
        try {
            List<Field> fields = ReflectUtil.getFieldByAnnotation(et.getClass(), TableId.class);
            if (!fields.isEmpty()) {
                return fields.get(0).get(et);
            }

            Field field = ReflectUtil.getFieldByName(et.getClass(), "id");
            if (field == null) {
                return null;
            }
            return field.get(et);
        } catch (IllegalAccessException e) {
            throw new NotProcessException("获取实体的id失败", e);
        }
    }

    private Object findEntityId(Object et) {
        Object entityId = getEntityId(et);
        if (entityId == null) {
            throw new NotProcessException("没有找到实体的id");
        }
        return entityId;
    }

    private ArrayList<String> getResource(Object et) {
        ArrayList<String> resources = new ArrayList<>();
        List<Field> fields = ReflectUtil.getFieldByAnnotation(et.getClass(), OmsResource.class);
        if (fields.isEmpty()) {
            throw new NotProcessException("没有找到资源字段");
        }
        for (Field field : fields) {
            String resource = (String) getFieldValue(field, et);
            resources.addAll(OmsUtil.convert2List(resource));
        }
        return resources;
    }

    private Object getFieldValue(Field field, Object et) {
        try {
            return field.get(et);
        } catch (IllegalAccessException e) {
            throw new OmsException("获取资源失败", e);
        }
    }

    /**
     * 获取MappedStatement的id
     *
     * @param ms MappedStatement
     * @return id
     */
    private String getStatementId(MappedStatement ms) {
        String[] splits = ms.getId().split("\\.");
        return splits[splits.length - 1];
    }

    /**
     * 获取目标MappedStatement
     *
     * @param ms         原始MappedStatement
     * @param methodName 方法名
     * @return 目标MappedStatement
     */
    private MappedStatement getTargetStatement(MappedStatement ms, String methodName) {
        String[] splits = ms.getId().split("\\.");
        splits[splits.length - 1] = methodName;
        String selectSqlId = String.join(".", splits);
        return ms.getConfiguration().getMappedStatement(selectSqlId);
    }

    private static class NotProcessException extends RuntimeException {
        public NotProcessException(String message) {
            super(message);
        }

        public NotProcessException(String message, Throwable e) {
            super(message, e);
        }
    }
}
