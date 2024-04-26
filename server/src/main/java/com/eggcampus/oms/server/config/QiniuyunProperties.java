package com.eggcampus.oms.server.config;

import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

/**
 * 自定义的七牛云属性
 *
 * @author 黄磊
 */
@Data
@Validated
@ConfigurationProperties(prefix = "qiniuyun")
public class QiniuyunProperties implements InitializingBean {
    @NotEmpty(message = "七牛云的accessKey不能为空")
    private String accessKey;
    @NotEmpty(message = "七牛云的secretKey不能为空")
    private String secretKey;
    @NotEmpty(message = "七牛云的bucket不能为空")
    private String bucket;

    /**
     * 存储空间对应的域名
     */
    private String domainOfBucket;
    /**
     * 回调地址的域名
     */
    private String ossCallbackDomain;

    /**
     * 存储空间所在的区域
     */
    private QiniuyunRegion region = QiniuyunRegion.AUTO;
    @Setter(AccessLevel.NONE)
    private Configuration bucketRegion = new Configuration(Region.autoRegion());

    /**
     * 上传凭证过期过期时间
     */
    @Min(value = 1, message = "上传凭证过期时间不能小于1")
    private Integer uploadExpireSecond = 900;
    /**
     * 私有库下载链接过期时间
     */
    @Min(value = 1, message = "私有库下载链接过期时间不能小于1")
    private Integer downloadExpireSecond = 3600;

    /**
     * 默认的文件/图像/视频大小限制
     */
    private DataSize sizeLimit = DataSize.ofMegabytes(5);
    /**
     * 文件大小限制
     * <p>
     * 假如为null，那么文件大小限制等于sizeLimit
     * </p>
     */
    private DataSize fileSizeLimit;
    /**
     * 图像大小限制
     * <p>
     * 假如为null，那么图像大小限制等于sizeLimit
     * </p>
     */
    private DataSize imageSizeLimit;
    /**
     * 视频大小限制
     * <p>
     * 假如为null，那么视频限制等于sizeLimit
     * </p>
     */
    private DataSize videoSizeLimit;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.fileSizeLimit == null) {
            this.fileSizeLimit = this.sizeLimit;
        }
        if (this.imageSizeLimit == null) {
            this.imageSizeLimit = this.sizeLimit;
        }
        if (this.videoSizeLimit == null) {
            this.videoSizeLimit = this.sizeLimit;
        }

        this.bucketRegion = switch (this.region) {
            case ZHE_JIANG1 -> new Configuration(Region.huadong());
            case ZHE_JIANG2 -> new Configuration(Region.huadongZheJiang2());
            case HE_BEI -> new Configuration(Region.huabei());
            case GUANG_DONG -> new Configuration(Region.huanan());
            case LOS_ANGELES -> new Configuration(Region.beimei());
            case SINGAPORE -> new Configuration(Region.xinjiapo());
            default -> new Configuration(Region.autoRegion());
        };
    }

    enum QiniuyunRegion {
        /**
         * 华东-浙江
         */
        ZHE_JIANG1,
        /**
         * 华东-浙江2
         */
        ZHE_JIANG2,
        /**
         * 华北-河北
         */
        HE_BEI,
        /**
         * 华南-广东
         */
        GUANG_DONG,
        /**
         * 北美-洛杉矶
         */
        LOS_ANGELES,
        /**
         * 亚太-新加坡
         */
        SINGAPORE,
        /**
         * 亚太-河内
         * <p>
         * 注意：七牛云官方没有提供河内的区域，所以河内使用自动区域
         * </p>
         */
        HANOI,
        /**
         * 自动
         */
        AUTO;
    }
}
