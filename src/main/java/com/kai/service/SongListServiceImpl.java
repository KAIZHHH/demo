package com.kai.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kai.dao.SongDao;
import com.kai.dao.SongListDao;
import com.kai.entity.QueryInfo;
import com.kai.entity.SongList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author kai
 * @date 2021/8/20 上午11:23
 */
@Service
@Transactional
@CacheConfig(cacheNames = "songlistCache")
public class SongListServiceImpl implements SongListService {

    @Autowired
    private SongListDao songListDao;

    @Autowired
    private SongDao songDao;

    /**
     * 添加SongList
     *
     * @param songList
     * @return
     */
    @Override
    @CacheEvict(value = "allSongLists", allEntries = true)
    public int insert(SongList songList) {
        int insert = songListDao.insert(songList);
        return insert;
    }

    /**
     * 根据id删除SongList
     *
     * @param id
     * @return
     */
    @Override
    @CacheEvict(value = "allSongLists", allEntries = true)
    public int deleteById(Integer id) {
        int delete = songListDao.deleteById(id);
        return delete;
    }

    /**
     * 根据id更新SongList
     *
     * @param songList
     * @return
     */
    @Override
    @CacheEvict(value = "allSongLists", allEntries = true)
    public int updateById(SongList songList) {
        int update = songListDao.updateById(songList);
        return update;
    }

    /**
     * 根据id查询SongList
     *
     * @param id
     * @return
     */
    @Override
    public SongList selectById(Integer id) {
        SongList songList = songListDao.selectById(id);
        return songList;
    }

    /**
     * 有条件的查询list<SongList>
     *
     * @param queryInfo
     * @return
     */
    @Override
    @Cacheable(value = "allSongLists", keyGenerator = "myKeyGenerator", unless = "#result == null")
    public PageInfo<SongList> selectList(QueryInfo queryInfo) {
        PageHelper.startPage(queryInfo.getPageNum(), queryInfo.getPageSize());
        QueryWrapper<SongList> wrapper = new QueryWrapper<>();
        wrapper.like("title", queryInfo.getQuery());
//        查询存放SongList数量的集合oldSongLists
        List<SongList> oldSongLists = songListDao.selectList(wrapper);

        PageInfo<SongList> pageInfo = new PageInfo<>(oldSongLists);
        List<SongList> songLists = pageInfo.getList();

        return pageInfo;
    }

    /**
     * 批量删除SongList
     *
     * @param ids
     * @return
     */
    @Override
    @CacheEvict(value = "allSongLists", allEntries = true)
    public int deleteSome(List<Integer> ids) {
        int i = songListDao.deleteBatchIds(ids);
        return i;
    }

    /**
     * 批量查找SongList
     *
     * @param ids
     * @return
     */
    @Override
    public List<SongList> getListSongList(List<Integer> ids) {
        List<SongList> songLists = songListDao.selectBatchIds(ids);
        return songLists;
    }
}
