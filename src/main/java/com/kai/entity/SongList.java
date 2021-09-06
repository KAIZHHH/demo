package com.kai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author kai
 * @date 2021/8/20 上午11:12
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("song_list")
public class SongList {

    /**
     * 歌单表的主键
     */
    @TableId(type = IdType.AUTO)
    private Integer songListId;
    /**
     * 歌单的标题
     */
    private String title;
    /**
     * 歌单封面URL
     */
    private String pic;
    /**
     * 歌单的简介
     */
    private String introduction;
    /**
     * 歌曲的风格
     */
    private String style;
    /**
     * 每个歌单中分别有多少个歌曲
     */
    @TableField(exist = false)
    private Integer songCount;

}
