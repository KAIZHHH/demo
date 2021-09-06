package com.kai.service;

import com.github.pagehelper.PageInfo;
import com.kai.entity.QueryInfo;
import com.kai.entity.Singer;
import org.springframework.cache.annotation.CacheConfig;

import java.util.List;

/**
 * @author kai
 * @date 2021/8/20 上午11:10
 */
public interface SingerService {

    /**
     * 添加Singer
     * @param singer
     * @return
     */
    int insert(Singer singer);

    /**
     * 根据id删除Singer
     * @param id
     * @return
     */
    int deleteById(Integer id);

    /**
     * 根据id更新Singer
     * @param singer
     * @return
     */
    int updateById(Singer singer);

    /**
     * 根据id查询Singer
     * @param id
     * @return
     */
    Singer selectById(Integer id);

    /**
     * 有条件的查询list<Singer>
     * @param queryInfo
     * @return
     */
    PageInfo<Singer> selectList(QueryInfo queryInfo);

    /**
     * 批量删除Singer
     * @param ids
     * @return
     */
    int deleteSome(List<Integer> ids);

    /**
     * 批量查找Singer
     * @param ids
     * @return
     */
    List<Singer> getListSinger(List<Integer> ids);

}
