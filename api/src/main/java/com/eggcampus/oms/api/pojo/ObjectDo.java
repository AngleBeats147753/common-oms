package com.eggcampus.oms.api.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.campus.util.springboot.enums.named.NamedEnumDeserializer;
import com.campus.util.springboot.mybatisplus.BaseEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 图像
 * </p>
 *
 * @author 黄磊
 * @since 2024-01-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("object")
public class ObjectDo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    public static final String ID = "id";
    public static final String TYPE = "type";
    public static final String URL = "url";
    public static final String USAGE_STATUS = "usage_status";
    public static final String USAGE_NUM = "usage_num";
    public static final String GENERATED_TIME = "generated_time";
    public static final String USED_TIME = "used_time";
    public static final String UPLOADED_TIME = "uploaded_time";
    public static final String MARKED_DELETION_TIME = "marked_deletion_time";
    public static final String DELETION_REASON = "deletion_reason";
    public static final String APPLICATION_ID = "application_id";
    public static final String VERSION = "version";
    public static final String DELETED = "deleted";

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 对象类型
     */
    @TableField("type")
    private Type type;

    /**
     * 图像URL
     */
    @TableField("url")
    private String url;

    /**
     * 使用状态
     */
    @TableField("usage_status")
    private UsageStatus usageStatus;

    /**
     * 使用次数
     */
    @TableField("usage_num")
    private Integer usageNum;

    /**
     * 生成时间
     */
    @TableField("generated_time")
    private LocalDateTime generatedTime;

    /**
     * 使用时间
     */
    @TableField(value = "used_time", updateStrategy = FieldStrategy.ALWAYS)
    private LocalDateTime usedTime;

    /**
     * 上传时间
     */
    @TableField("uploaded_time")
    private LocalDateTime uploadedTime;

    /**
     * 待删除时间
     */
    @TableField(value = "marked_deletion_time", updateStrategy = FieldStrategy.ALWAYS)
    private LocalDateTime markedDeletionTime;

    /**
     * 删除原因
     */
    @TableField(value = "deletion_reason", updateStrategy = FieldStrategy.ALWAYS)
    private String deletionReason;

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

    /**
     * 使用状态
     */
    @Getter
    @JsonDeserialize(using = NamedEnumDeserializer.class)
    public enum UsageStatus implements BaseEnum<Integer> {
        /**
         * 已生成
         */
        GENERATED(0, "已生成"),
        /**
         * 已上传
         */
        UPLOADED(1, "已上传"),
        /**
         * 已使用
         */
        USED(2, "已使用"),
        /**
         * 已标记删除
         */
        DELETION_MARKED(3, "已标记删除"),
        /**
         * 已删除
         */
        DELETED(4, "已标记删除");

        private final Integer value;
        private final String name;

        UsageStatus(Integer value, String name) {
            this.value = value;
            this.name = name;
        }
    }

    /**
     * 对象类型
     */
    @Getter
    @JsonDeserialize(using = NamedEnumDeserializer.class)
    public enum Type implements BaseEnum<Integer> {
        IMAGE(1, "图像");
        private final Integer value;
        @JsonValue
        private final String name;

        Type(Integer value, String name) {
            this.value = value;
            this.name = name;
        }
    }
}
