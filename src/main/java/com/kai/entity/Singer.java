package com.kai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author kai
 * @date 2021/8/20 上午10:52
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("singer")
public class Singer implements Serializable {

    /**
     * 歌手表的主键
     */
    @TableId(type = IdType.AUTO)
    private Integer singerId;
    /**
     * 歌手姓名
     */
    private String name;
    /**
     * 歌手性别（1：男，2：女，3：组合）
     */
    private String sex;
    /**
     * 歌手头像URL
     */
    private String pic;
    /**
     * 歌手生日
     */
//    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    private Date birth;
    /**
     * 歌手所属地区
     */
    private String location;
    /**
     * 歌手简介
     */
    private String introduction;
    /**
     * 有多少个歌曲
     */
    @TableField(exist = false)
    private Integer songCount;
}
