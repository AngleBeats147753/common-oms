package com.eggcampus.oms.server.pojo.qo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author huangshuaijie
 * @date 2024/2/15 13:44
 */
@Data
public class BatchDeleteQO {

    @NotEmpty
    private List<DeleteQO> deleteQoList;
}
