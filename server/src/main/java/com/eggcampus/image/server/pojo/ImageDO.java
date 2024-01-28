package com.eggcampus.image.server.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.eggcampus.util.mybatis.enums.BaseEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 图像
 * </p>
 *
 * @author 黄磊
 * @since 2024-01-28
 */
@Data
@TableName("image")
public class ImageDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    public static final String ID = "id";
    public static final String URL = "url";
    public static final String USED = "used";
    public static final String CHECK_STATUS = "check_status";
    public static final String USED_TIME = "used_time";
    public static final String APPLICATION_ID = "application_id";
    public static final String VERSION = "version";
    public static final String DELETED = "deleted";

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 图像URL
     */
    @TableField("url")
    private String url;

    /**
     * 是否已使用
     */
    @TableField("used")
    private Boolean used;

    /**
     * 审核状态（0-未审核,1-审核中,2-审核未通过,3-审核通过,4-无需审核）
     */
    @TableField("check_status")
    private CheckStatus checkStatus;

    /**
     * 使用时间
     */
    @TableField("used_time")
    private LocalDateTime usedTime;

    /**
     * 应用id
     */
    @TableField("application_id")
    private Long applicationId;

    /**
     * 乐观锁
     */
    @TableField("version")
    @Version
    @JsonIgnore
    private Long version;

    /**
     * 逻辑删除（逻辑删除(0-未删除，id号-已删除)）
     */
    @TableField("deleted")
    @TableLogic
    @JsonIgnore
    private Long deleted;

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
}
