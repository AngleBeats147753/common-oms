package com.eggcampus.oms.client.springboot;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 黄磊
 */
public class BeanUtil {

    private static final Map<Class<?>, Map<Class<?>, List<Field>>> ANNOTATION_FIELD = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Map<String, Field>> NAME_FIELD = new ConcurrentHashMap<>();

    /**
     * 获取类中所有被指定注解标记的字段，并且所有字段都会被设置为可访问
     *
     * @param clazz           类
     * @param annotationClass 注解
     * @return 字段列表
     */
    public static List<Field> getFieldByAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        Map<Class<?>, List<Field>> map = ANNOTATION_FIELD.computeIfAbsent(clazz, k -> new ConcurrentHashMap<>());
        return map.computeIfAbsent(annotationClass, k -> {
            ArrayList<Field> fields = new ArrayList<>();
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(annotationClass)) {
                    field.setAccessible(true);
                    fields.add(field);
                }
            }
            return fields;
        });
    }

    /**
     * 获取类中指定名称的字段，并且字段会被设置为可访问
     *
     * @param clazz     类
     * @param fieldName 字段名称
     * @return 字段
     */
    public static Field getFieldByName(Class<?> clazz, String fieldName) {
        Map<String, Field> map = NAME_FIELD.computeIfAbsent(clazz, k -> new ConcurrentHashMap<>());
        return map.computeIfAbsent(fieldName, k -> {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getName().equals(fieldName)) {
                    field.setAccessible(true);
                    return field;
                }
            }
            return null;
        });
    }
}
