package com.eggcampus.oms.client.springboot;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.eggcampus.oms.api.pojo.qo.UsageQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author 黄磊
 */
@Slf4j
public class OmsInnerInterceptor implements InnerInterceptor {
    private final OmsManager omsManager;

    public OmsInnerInterceptor(OmsManager omsManager) {
        this.omsManager = omsManager;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void beforeUpdate(Executor executor, MappedStatement ms, Object parameter) {
        // 参数都没有，那么肯定不是oms要处理的
        if (parameter == null) {
            return;
        }
        try {
            if (SqlCommandType.INSERT == ms.getSqlCommandType()) {
                Object et = getEntity((Map<String, Object>) parameter);
                insert(et);
            } else if (SqlCommandType.UPDATE == ms.getSqlCommandType()) {
                // update语句的参数不能只有一个基本类型
                if (!(parameter instanceof Map)) {
                    return;
                }
                Object et = getEntity((Map<String, Object>) parameter);
                update(executor, ms, et);
            } else if (SqlCommandType.DELETE == ms.getSqlCommandType()) {
                Object id = parameter;
                if (parameter instanceof Map) {
                    Object et = getEntity((Map<String, Object>) parameter);
                    id = getId(et);
                }
                delete(executor, ms, id);
            }
        } catch (NotProcessException e) {
            log.debug("不进行oms处理", e);
        }
    }

    private Object getEntity(Map<String, Object> map) {
        Object et = map.getOrDefault(Constants.ENTITY, null);
        if (et == null) {
            throw new NotProcessException("没有找到ENTITY实体类");
        }
        return map.getOrDefault(Constants.ENTITY, null);
    }

    private Object getId(Object et) {
        try {
            List<Field> fields = BeanUtil.getFieldByAnnotation(et.getClass(), TableId.class);
            if (!fields.isEmpty()) {
                return fields.get(0).get(et);
            }

            Field field = BeanUtil.getFieldByName(et.getClass(), "id");
            if (field == null) {
                throw new NotProcessException("没有找到id字段");
            }
            return field.get(et);
        } catch (IllegalAccessException e) {
            throw new NotProcessException("获取实体的id失败", e);
        }
    }

    private void insert(Object et) {
        List<Field> fields = BeanUtil.getFieldByAnnotation(et.getClass(), OmsResource.class);
        if (fields.isEmpty()) {
            return;
        }
        for (Field field : fields) {
            try {
                String resources = (String) field.get(et);
                omsManager.use(new UsageQuery(resources, false));
            } catch (IllegalAccessException e) {
                throw new NotProcessException("获取实体的资源失败", e);
            }
        }
    }

    private void update(Executor executor, MappedStatement ms, Object et) {
        List<Field> fields = BeanUtil.getFieldByAnnotation(et.getClass(), OmsResource.class);
        if (fields.isEmpty()) {
            return;
        }
        Object id = getId(et);
        Object oldEntity = getOldEntity(executor, ms, id);
        for (Field field : fields) {
            try {
                String oldResources = (String) field.get(oldEntity);
                String newResources = (String) field.get(et);
                omsManager.change(oldResources, newResources);
            } catch (IllegalAccessException e) {
                throw new NotProcessException("获取实体的资源失败", e);
            }
        }
    }

    private void delete(Executor executor, MappedStatement ms, Object id) {
        Object oldEntity = getOldEntity(executor, ms, id);
        List<Field> fields = BeanUtil.getFieldByAnnotation(oldEntity.getClass(), OmsResource.class);
        if (fields.isEmpty()) {
            return;
        }
        for (Field field : fields) {
            try {
                String resources = (String) field.get(oldEntity);
                omsManager.delete(resources);
            } catch (IllegalAccessException e) {
                throw new NotProcessException("获取实体的资源失败", e);
            }
        }
    }

    /**
     * 获取数据库中的实体
     */
    private Object getOldEntity(Executor executor, MappedStatement ms, Object id) {
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

    private static class NotProcessException extends RuntimeException {
        public NotProcessException(String message) {
            super(message);
        }

        public NotProcessException(String message, Throwable e) {
            super(message, e);
        }
    }
}
