package com.eggcampus.object.server.pojo.qo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author huangshuaijie
 * @date 2024/2/15 13:44
 */
@Data
public class BatchUsageQO {

    @NotNull
    private List<UsageQO> usageQoList;
}
