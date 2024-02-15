package com.eggcampus.object.enums;

import com.eggcampus.util.spring.mybatisplus.enums.BaseEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

/**
 * @author huangshuaijie
 * @date 2024/2/15 15:07
 */
@JsonDeserialize
public enum ObjectTypeEnum implements BaseEnum<Integer> {
    IMAGE(1, "图像")
    ;

    private final Integer value;
    @Getter
    @JsonValue
    private final String name;

    ObjectTypeEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
