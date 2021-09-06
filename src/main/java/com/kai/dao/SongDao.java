package com.kai.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kai.entity.Song;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author hjf
 * @create 2020-11-05 19:30
 */
@Repository
public interface SongDao extends BaseMapper<Song> {

}
