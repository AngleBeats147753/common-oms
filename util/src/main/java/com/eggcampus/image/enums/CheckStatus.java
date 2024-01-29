package com.eggcampus.image.enums;

import com.eggcampus.util.mybatis.enums.BaseEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 黄磊
 */
@JsonDeserialize
public enum CheckStatus implements BaseEnum<Integer> {
    /**
     * 未审核
     */
    UNCHECKED(0, "未审核"),
    /**
     * 审核中
     */
    CHECKING(1, "审核中"),
    /**
     * 审核未通过
     */
    CHECK_FAILED(2, "审核未通过"),
    /**
     * 审核通过
     */
    CHECK_SUCCESS(3, "审核通过"),
    /**
     * 无需审核
     */
    NO_NEED_CHECK(4, "无需审核");

    private final Integer index;
    @Getter
    @JsonValue
    private final String name;

    CheckStatus(Integer index, String name) {
        this.index = index;
        this.name = name;
    }

    private static final Map<String, CheckStatus> MAP = new HashMap<>();

    static {
        for (CheckStatus type : CheckStatus.values()) {
            MAP.put(type.name, type);
        }
    }

    @JsonCreator
    public static CheckStatus createByName(String name) {
        CheckStatus status = MAP.get(name);
        if (status == null) {
            throw new IllegalArgumentException("Status not found. name=" + name);
        }
        return MAP.get(name);
    }

    @Override
    public Integer getValue() {
        return this.index;
    }
}