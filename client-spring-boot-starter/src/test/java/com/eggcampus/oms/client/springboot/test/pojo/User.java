package com.eggcampus.oms.client.springboot.test.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eggcampus.oms.client.springboot.OmsResource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_user")
public class User implements Cloneable {
    @TableId(type = IdType.AUTO)
    private Long id;
    @OmsResource
    private String images;
    private Integer age;
    private String email;
    private Long deleted;

    @Override
    public User clone() {
        try {
            User clone = (User) super.clone();
            clone.setId(this.id);
            clone.setImages(this.images);
            clone.setAge(this.age);
            clone.setEmail(this.email);
            clone.setDeleted(this.deleted);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
