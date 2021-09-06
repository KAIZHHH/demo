package com.kai.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kai.dao.SingerDao;
import com.kai.dao.SongDao;
import com.kai.entity.QueryInfo;
import com.kai.entity.Singer;
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
@CacheConfig(cacheNames = "singerCache")
public class SingerServiceImpl implements SingerService {


    @Autowired
    private SingerDao singerDao;

    @Autowired
    private SongDao songDao;

    /**
     * 添加Singer
     *
     * @param singer
     * @return
     */
    @Override
    @CacheEvict(value = "allSingers", allEntries = true)
    public int insert(Singer singer) {
        int insert = singerDao.insert(singer);
        return insert;
    }


    /**
     * 根据id更新Singer
     *
     * @param singer
     * @return
     */
    @Override
    /**
     * 更新用户，同时使用新的返回值的替换缓存中的值
     * 更新用户后会将allUsersCache缓存全部清空
     */
    @CacheEvict(value = "allSingers", allEntries = true)
    public int updateById(Singer singer) {
        int update = singerDao.updateById(singer);
        return update;
    }

    /**
     * 根据id查询Singer
     *
     * @param id
     * @return
     */
    @Override
    public Singer selectById(Integer id) {
        Singer singer = singerDao.selectById(id);
        return singer;
    }

    /**
     * 有条件的查询list<Singer>
     *
     * @param queryInfo
     * @return
     */
    @Override
    @Cacheable(value = "allSingers", keyGenerator = "myKeyGenerator", unless = "#result == null")
    public PageInfo<Singer> selectList(QueryInfo queryInfo) {
        // 设置初始页,和每页查找数量
        PageHelper.startPage(queryInfo.getPageNum(), queryInfo.getPageSize());
        //模糊查询
        QueryWrapper<Singer> wrapper = new QueryWrapper<>();
        wrapper.like("name", queryInfo.getQuery());

        List<Singer> oldSingers = singerDao.selectList(wrapper);
        PageInfo<Singer> pageInfo = new PageInfo<>(oldSingers);

        return pageInfo;
    }

    /**
     * 根据id删除Singer
     *
     * @param id
     * @return
     */
    @Override
    /**
     * 对符合key条件的记录从缓存中移除
     * 删除用户后会将allUsersCache缓存全部清空
     */
    @CacheEvict(value = "allSingers", allEntries = true)
    public int deleteById(Integer id) {
        int delete = singerDao.deleteById(id);
        return delete;
    }

    /**
     * 批量删除Singer
     *
     * @param ids
     * @return
     */
    @Override
    @CacheEvict(value = "allSingers", allEntries = true)
    public int deleteSome(List<Integer> ids) {
        int i = singerDao.deleteBatchIds(ids);
        return i;
    }

    /**
     * 批量查找Singer
     *
     * @param ids
     * @return
     */
    @Override
    public List<Singer> getListSinger(List<Integer> ids) {
        List<Singer> singers = singerDao.selectBatchIds(ids);
        return singers;
    }
}
