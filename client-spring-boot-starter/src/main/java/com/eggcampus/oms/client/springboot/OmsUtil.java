package com.eggcampus.oms.client.springboot;

import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author 黄磊
 */
public class OmsUtil {
    public static String convert2Str(List<String> list) {
        if (list == null) {
            return "";
        }
        return StrUtil.join(",", list);
    }

    public static List<String> convert2List(String str) {
        if (StrUtil.isEmpty(str)) {
            return new ArrayList<>();
        }
        return StrUtil.split(str, ",");
    }

    public static ObjectStateTracker compare(Collection<String> oldObjects, Collection<String> newObjects) {
        List<String> stableObjects = new ArrayList<>();
        List<String> deletedObjects = new ArrayList<>();
        List<String> addedObjects = new ArrayList<>();
        if (oldObjects != null) {
            for (String oldImage : oldObjects) {
                if (newObjects.contains(oldImage)) {
                    stableObjects.add(oldImage);
                } else {
                    deletedObjects.add(oldImage);
                }
            }
        }
        if (newObjects != null) {
            for (String newImage : newObjects) {
                if (!stableObjects.contains(newImage)) {
                    addedObjects.add(newImage);
                }
            }
        }
        return new ObjectStateTracker(addedObjects, deletedObjects, stableObjects);
    }
}
