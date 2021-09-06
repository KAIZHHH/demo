package com.kai.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kai.dao.UserDao;
import com.kai.entity.QueryInfo;
import com.kai.entity.User;
import com.kai.utils.SaltUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * @author kai
 * @date 2021/7/24 下午9:17
 */


@Service("userService")
@Transactional
@Slf4j
@CacheConfig(cacheNames = "userCache")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;


    @Override
    public User selectOne(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userDao.selectOne(queryWrapper);
        return user;
    }

    @Override
    /**
     * 创建用户，同时使用新的返回值的替换缓存中的值
     * 创建用户后会将allUsersCache缓存全部清空
     */

    public void insert(User user) {
        //处理业务调用dao
        //1.生成随机盐
        String salt = SaltUtils.getSalt(8);
        log.info("salt:" + salt);
        //2.将随机盐保存到数据
        user.setSalt(salt);
        //3.明文密码进行md5 + salt + hash散列
        Md5Hash md5Hash = new Md5Hash(user.getPassword(), salt, 1024);
        user.setPassword(md5Hash.toHex());
        log.info("Password:" + user.getPassword());
//        默认注册是用户,role=2
        user.setRole(2);

        userDao.insert(user);
    }


    /**
     * 有条件的查询list<User>
     *
     * @param pageNum   当前页号
     * @param pageSize  设置每页显示数量
     * @param queryInfo
     * @return
     */
    @Override
    @Cacheable(value = "allUsers", keyGenerator = "myKeyGenerator", unless = "#result == null")
    public PageInfo<User> selectList(QueryInfo queryInfo) {
        // 设置初始页,和每页查找数量
        PageHelper.startPage(queryInfo.getPageNum(), queryInfo.getPageSize());
        //模糊查询
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like("username", queryInfo.getQuery());
        List<User> users = userDao.selectList(wrapper);

        PageInfo<User> pageInfo = new PageInfo<>(users);
        return pageInfo;
    }

    @Override

    /**
     * 更新用户，同时使用新的返回值的替换缓存中的值
     * 更新用户后会将allUsersCache缓存全部清空
     */
//    @Caching(
//            put = {@CachePut(value = "userCache", key = "#user.id")},
//            evict = {@CacheEvict(value = "allUsersCache", allEntries = true)}
//    )
    @CacheEvict(value = "allUsers", allEntries = true)
    public int updateById(User user) {
        int update = userDao.updateById(user);
        return update;
    }

    @Override
    public User selectById(Integer id) {
        User user = userDao.selectById(id);
        return user;

    }

    @Override
    public List<User> getListUser(List<Integer> ids) {
        List<User> users = userDao.selectBatchIds(ids);
        return users;
    }

    @Override
    /**
     * 对符合key条件的记录从缓存中移除
     * 删除用户后会将allUsersCache缓存全部清空
     */
    @CacheEvict(value = "allUsers", allEntries = true)
    public int deleteById(Integer id) {
        int delete = userDao.deleteById(id);
        return delete;
    }

    @Override
    @CacheEvict(value = "allUsers", allEntries = true)
    public int deleteSome(List<Integer> ids) {
        int i = userDao.deleteBatchIds(ids);
        return i;
    }
}
