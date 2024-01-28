package com.eggcampus.image.server.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

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
@TableName("application")
public class ApplicationDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String PROFILE = "profile";
    public static final String PATH_PREFIX = "path_prefix";
    public static final String VERSION = "version";
    public static final String DELETED = "deleted";

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 应用名称
     */
    @TableField("name")
    private String name;

    /**
     * 应用环境
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


}
