package com.kai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author kai
 * @date 2021/9/3 上午7:57
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("song")
public class Song implements Serializable {
    private Integer songId;
    private Integer singId;
    private Integer songListId;
    private String name;
    private String introduction;
    private String pic;
    private String url;
    /**
     * 歌曲对应的歌手
     */
    @TableField(exist = false)
    private Singer singer;
    /**
     * list_song表的主键
     */
    @TableField(exist = false)
    private String listSongId;


}
