package com.kai.service;

import com.github.pagehelper.PageInfo;
import com.kai.entity.QueryInfo;
import com.kai.entity.SongList;

import java.util.List;

/**
 * @author kai
 * @date 2021/8/20 上午11:10
 */
public interface SongListService {

    /**
     * 添加SongList
     *
     * @param songList
     * @return
     */
    int insert(SongList songList);

    /**
     * 根据id删除SongList
     *
     * @param id
     * @return
     */
    int deleteById(Integer id);

    /**
     * 根据id更新SongList
     *
     * @param songList
     * @return
     */
    int updateById(SongList songList);

    /**
     * 根据id查询SongList
     *
     * @param id
     * @return
     */
    SongList selectById(Integer id);

    /**
     * 有条件的查询list<SongList>
     *
     * @param queryInfo
     * @return
     */
    PageInfo<SongList> selectList(QueryInfo queryInfo);

    /**
     * 删除SongList
     *
     * @param ids
     * @return
     */
    int deleteSome(List<Integer> ids);

    /**
     * 批量查找SongList
     *
     * @param ids
     * @return
     */
    List<SongList> getListSongList(List<Integer> ids);
}
