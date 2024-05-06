package com.eggcampus.oms.client.springboot.mybatis;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.eggcampus.oms.client.springboot.OmsResource;
import lombok.Data;

@Data
public class SysUser {
    @TableId
    private Long id;
    @OmsResource
    private String name;
    private Integer age;
    private String email;
    @TableLogic
    private Long deleted;
}
