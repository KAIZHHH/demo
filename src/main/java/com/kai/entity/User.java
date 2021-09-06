package com.kai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("user")
@ToString
public class User implements Serializable {
    @TableId(type = IdType.AUTO)
    private int id;
    private String username;
    private String password;
    private String pic;
    private String salt;
    private Integer sex;

    private String phone;
    private String region;
    private String signature;

    private Integer role;



}
