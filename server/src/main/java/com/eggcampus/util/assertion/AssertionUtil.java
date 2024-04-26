package com.eggcampus.util.assertion;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.UtilException;

import java.util.List;

/**
 * 公共断言工具
 *
 * @author 黄磊
 */
public class AssertionUtil {

    /**
     * 断言列表相同
     *
     * @param list1 列表1
     * @param list2 列表2
     * @param <T>   列表元素类型
     */
    public static <T> void assertListEqual(List<T> list1, List<T> list2) {
        if (!CollUtil.isEqualList(list1, list2)) {
            throw new UtilException("两个列表有元素不同。列表1<%s>，列表2<%s>".formatted(list1, list2));
        }
    }
}
