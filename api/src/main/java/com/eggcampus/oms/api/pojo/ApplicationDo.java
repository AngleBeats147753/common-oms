package com.eggcampus.oms.api.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.campus.util.springboot.enums.named.NamedEnumDeserializer;
import com.campus.util.springboot.mybatisplus.BaseEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 应用
 * </p>
 *
 * @author 黄磊
 * @since 2024-01-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("application")
public class ApplicationDo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    public static final String ID = "id";
    public static final String PROJECT_NAME = "project_name";
    public static final String PROFILE = "profile";
    public static final String PATH_PREFIX = "path_prefix";
    public static final String SHARE_LEVEL = "share_level";
    public static final String VERSION = "version";
    public static final String DELETED = "deleted";

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 项目名
     */
    @TableField("project_name")
    private String projectName;

    /**
     * 环境
     */
    @TableField("profile")
    private String profile;

    /**
     * 保存到OSS时的路径前缀
     * <p>
     * 当OSS为七牛云时，不能以斜杠开头，但必须以斜杠结尾
     * </p>
     */
    @TableField("path_prefix")
    private String pathPrefix;

    /**
     * 共享等级
     */
    @TableField("share_level")
    private ShareLevel shareLevel;

    /**
     * 乐观锁
     */
    @TableField("version")
    @Version
    @JsonIgnore
    private Long version;

    /**
     * 逻辑删除（0-未删除，id号-已删除）
     */
    @TableField("deleted")
    @TableLogic
    @JsonIgnore
    private Long deleted;

    @Getter
    @JsonDeserialize(using = NamedEnumDeserializer.class)
    public enum ShareLevel implements BaseEnum<Integer> {
        NONE(0, "不共享"),
        /**
         * 环境级别。可以被其他环境访问
         */
        PROFILE(1, "环境");
        private final Integer value;
        private final String name;

        ShareLevel(Integer value, String name) {
            this.value = value;
            this.name = name;
        }
    }
}
