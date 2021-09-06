package com.kai.service;

import com.github.pagehelper.PageInfo;
import com.kai.entity.QueryInfo;
import com.kai.entity.User;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * @author kai
 * @date 2021/7/24 下午9:18
 */

public interface UserService {

    /**
     * 用于登录时查询是否存在此User对象
     *
     * @param username
     * @return
     */
    User selectOne(String username);

    /**
     * 用于注册时添加User
     *
     * @param user
     * @return
     */
    void insert(User user);


    /**
     * 根据id删除User
     *
     * @param id
     * @return
     */
    int deleteById(Integer id);

    /**
     * 根据id更新User
     *
     * @param user
     * @return
     */
    int updateById(User user);

    /**
     * 根据id查询User
     *
     * @param id
     * @return
     */
    User selectById(Integer id);

    /**
     * 有条件的查询list<User>
     *
     * @param queryInfo
     * @return
     */
    PageInfo<User> selectList(QueryInfo queryInfo);

    /**
     * 批量删除User
     *
     * @param ids
     * @return
     */
    int deleteSome(List<Integer> ids);

    /**
     * 批量查找User
     *
     * @param ids
     * @return
     */
    List<User> getListUser(List<Integer> ids);
}
