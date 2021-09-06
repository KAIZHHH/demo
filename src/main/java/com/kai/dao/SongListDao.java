package com.kai.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kai.entity.SongList;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author kai
 * @date 2021/9/2 上午10:44
 */
@Repository("songListDao")
public interface SongListDao extends BaseMapper<SongList> {

    /**
     * 模糊查询List<SongList>
     * 对SongList封装songCount(有多少个歌曲)
     * @param title
     * @return
     */
    List<SongList> selectSongLists(String title);
}
