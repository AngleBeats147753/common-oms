package com.eggcampus.oms.api.pojo.qo;

import com.eggcampus.oms.api.constant.DeletionReason;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

/**
 * @author 黄磊
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "objectUrl")
public class DeletionQo {
    @Length(max = 255, message = "objectUrl长度不能超过255")
    @NotEmpty(message = "objectUrl不能为null或者空字符串")
    private String objectUrl;
    @Length(max = 255, message = "objectUrl长度不能超过255")
    @NotEmpty(message = "deletionReason不能为null或者空字符串")
    private String deletionReason = DeletionReason.BUSINESS_DELETION;
}
