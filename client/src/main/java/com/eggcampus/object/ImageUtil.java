package com.eggcampus.object;

import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 黄磊
 */
public class ImageUtil {
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

    public static ImageStateTracker compare(List<String> oldImages, List<String> newImages) {
        List<String> stableImages = new ArrayList<>();
        List<String> deletedImages = new ArrayList<>();
        List<String> addedImages = new ArrayList<>();
        if (oldImages != null) {
            for (String oldImage : oldImages) {
                if (newImages.contains(oldImage)) {
                    stableImages.add(oldImage);
                } else {
                    deletedImages.add(oldImage);
                }
            }
        }
        if (newImages != null) {
            for (String newImage : newImages) {
                if (!stableImages.contains(newImage)) {
                    addedImages.add(newImage);
                }
            }
        }
        return new ImageStateTracker(addedImages, deletedImages, stableImages);
    }
}
