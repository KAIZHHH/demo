package com.kai.controller;

import com.github.pagehelper.PageInfo;
import com.kai.entity.QueryInfo;
import com.kai.entity.SongList;
import com.kai.result.ResultMap;
import com.kai.service.SongListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author kai
 * @date 2021/9/1 下午9:10
 */
@RestController
@RequestMapping("/songList")
public class SongListController {

    @Autowired
    private SongListService songListService;

    @Autowired
    private ResultMap resultMap;

    /**
     * 默认歌单图片
     */
    private static final String defaultSongListPic = "/img/songListPic/123.jpg";

    /**
     * 模糊及分页查询
     *
     * @param queryInfo
     * @return
     */
    @PostMapping("/get")
    public PageInfo<SongList> getAllSongLists(@RequestBody QueryInfo queryInfo) {
        PageInfo<SongList> pageInfo = songListService.selectList(queryInfo);
        return pageInfo;
    }

    /**
     * 查询一个歌单
     *
     * @param id
     * @return
     */
    @GetMapping("/getOne/{id}")
    public ResultMap getOneSongList(@PathVariable Integer id) {
        SongList songList = songListService.selectById(id);
        if (songList != null) {
            return resultMap.success().code(200).message(songList);
        }
        return resultMap.fail().code(401).message("歌单查找失败");
    }

    /**
     * 添加一个歌单
     *
     * @param songList
     * @return
     */
    @PostMapping("/add")
    public ResultMap addSongList(@RequestBody SongList songList) {
        songList.setPic(defaultSongListPic);// 设置默认歌单图片
        int insert = songListService.insert(songList);
        if (insert > 0) {
            return resultMap.success().code(200).message("歌单" + songList.getTitle() + "添加成功");
        }
        return resultMap.fail().code(401).message("歌单" + songList.getTitle() + "添加失败");
    }

    /**
     * 更新一个歌单
     *
     * @param songList
     * @return
     */
    @PutMapping("/update")
    public ResultMap updateSongList(@RequestBody SongList songList) {
        int update = songListService.updateById(songList);
        if (update > 0) {
            return resultMap.success().code(200).message("歌单" + songList.getTitle() + "更新成功");
        }
        return resultMap.fail().code(401).message("歌单" + songList.getTitle() + "更新失败");
    }

    /**
     * 删除一个歌单
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public ResultMap deleteSongList(@PathVariable Integer id) {
        SongList songList = songListService.selectById(id);
        deleteSongListPicFile(songList); // 删除歌单图片文件

        int delete = songListService.deleteById(id);
        if (delete > 0) {
            return resultMap.success().code(200).message("歌单[ " + songList.getTitle() + " ]删除成功");
        }
        return resultMap.fail().code(401).message("歌单[ " + songList.getTitle() + " ]删除失败");
    }

    /**
     * 更新歌单的图片
     *
     * @param multipartFile
     * @param id
     * @return
     */
    @PostMapping("/updateSongListPic/{id}")
    public ResultMap updateSongListPic(@RequestParam("file") MultipartFile multipartFile, @PathVariable int id) {
        //上传图片文件
        if (multipartFile.isEmpty()) {
            return resultMap.fail().code(401).message("图片上传失败");
        }
        if (!multipartFile.getContentType().equals("image/jpeg")) {
            return resultMap.fail().code(401).message("上传的文件类型错误");
        }

        // 文件名 = 当前时间到毫秒 + 原来的文件名
        String fileName = System.currentTimeMillis() + multipartFile.getOriginalFilename(); // 防止文件名重复
        // 文件路径
        String filePath = System.getProperty("user.dir") + System.getProperty("file.separator") + "img"
                + System.getProperty("file.separator") + "songListPic" + System.getProperty("file.separator");
        // 如果文件路径不存在，新增该路径
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdir();
        }
        // 实际的文件地址
        File dest = new File(filePath + fileName);
        // 存储到数据库里的相对文件地址
        String storeAvatorPath = "/img/songListPic/" + fileName;
        try {
            multipartFile.transferTo(dest);
            SongList songList = songListService.selectById(id);
            deleteSongListPicFile(songList); //如果当前歌单的图片不是默认图片，更新时，删除旧图片

            songList.setPic(storeAvatorPath); //数据库更新新的图片的地址
            int update = songListService.updateById(songList);
            if (update > 0) {
                return resultMap.success().code(200).message("图片更新成功");
            }
            return resultMap.fail().code(401).message("图片更新失败");
        } catch (IOException e) {
            return resultMap.fail().code(401).message("图片更新失败");
        }
    }

    /**
     * 批量删除歌单
     *
     * @param params
     * @return
     */
    @PostMapping("/delSome")
    public ResultMap delSome(@RequestBody Map params) {
        List<Integer> list = (List<Integer>) params.get("ids");
        List<SongList> listSongList = songListService.getListSongList(list);
        listSongList.forEach(songList -> {
            deleteSongListPicFile(songList); // 删除歌单图片文件
        });

        int i = songListService.deleteSome(list);
        if (i > 0) {
            return resultMap.success().code(200).message("批量删除成功");
        }
        return resultMap.fail().code(401).message("批量删除失败");
    }

    /**
     * 删除歌单图片文件
     *
     * @param songList
     */
    private void deleteSongListPicFile(SongList songList) {
        // 歌单图片地址（图片地址不等于默认图片地址时删掉歌单图片）
        if (!songList.getPic().equals(defaultSongListPic)) {
            String songPicPath = System.getProperty("user.dir") + songList.getPic();
            File songPicFile = new File(songPicPath);
            if (songPicFile.exists()) {
                songPicFile.delete();
            }
        }
    }

}
