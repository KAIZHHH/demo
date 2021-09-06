package com.kai.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kai.dao.SingerDao;
import com.kai.dao.SongDao;
import com.kai.dao.SongListDao;
import com.kai.dao.UserDao;
import com.kai.entity.Singer;
import com.kai.entity.SongList;
import com.kai.entity.User;
import com.kai.result.ResultMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;


/**
 * @author kai
 * @date 2021/8/22 下午6:57
 */
@RequestMapping("/info")
@RestController
@CrossOrigin //允许跨域 前后端分离
@Slf4j
@CacheConfig(cacheNames = "infoCache")
public class InfoController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SongDao songDao;

    @Autowired
    private SingerDao singerDao;

    @Autowired
    private SongListDao songListDao;

    @Autowired
    private ResultMap resultMap;

    @GetMapping("/getAllCount")
    @Cacheable(value = "allInfomations", keyGenerator = "myKeyGenerator", unless = "#result == null")
    public ResultMap getAllCount() {

//        用户数量
        Integer userCount = userDao.selectCount(null);
//        男性用户
        Integer boysCount = userDao.selectCount(new QueryWrapper<User>().eq("sex", 1));//备注1位男生
//        女性用户
        Integer girlsCount = userDao.selectCount(new QueryWrapper<User>().eq("sex", 2));//备注2为女生
//        歌曲数量
        Integer songCount = songDao.selectCount(null);
//        歌手数量
        Integer singerCount = singerDao.selectCount(null);
//        歌单数量
        Integer songListCount = songListDao.selectCount(null);

        HashMap<String, Object> map = new HashMap<>();
        map.put("userCount", userCount);
        map.put("girlsCount", girlsCount);
        map.put("boysCount", boysCount);
        map.put("songCount", songCount);
        map.put("singerCount", singerCount);
        map.put("songListCount", songListCount);


//        华语歌曲
        Integer huayu = songListDao.selectCount(new QueryWrapper<SongList>().eq("style", "华语"));
//        粤语歌曲
        Integer yueyu = songListDao.selectCount(new QueryWrapper<SongList>().eq("style", "粤语"));
//        轻音乐
        Integer BGM = songListDao.selectCount(new QueryWrapper<SongList>().eq("style", "BGM"));

        HashMap<String, Integer> style = new HashMap<>();
        style.put("huayu", huayu);
        style.put("yueyu", yueyu);
        style.put("BGM", BGM);
        map.put("style", style);//大map放进更大map


//        男性歌手
        Integer men = singerDao.selectCount(new QueryWrapper<Singer>().eq("sex", 1));
//       女性歌手
        Integer women = singerDao.selectCount(new QueryWrapper<Singer>().eq("sex", 2));
//        组合歌手
        Integer team = singerDao.selectCount(new QueryWrapper<Singer>().eq("sex", 3));

        HashMap<String, Integer> singerSex = new HashMap<>();
        singerSex.put("women", women);
        singerSex.put("men", men);
        singerSex.put("team", team);
        map.put("singerSex", singerSex);

        return resultMap.success().code(200).message(map);
    }


}
