package com.kai.controller;

import com.github.pagehelper.PageInfo;
import com.kai.entity.QueryInfo;
import com.kai.entity.Singer;
import com.kai.result.ResultMap;
import com.kai.service.SingerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author kai
 * @date 2021/8/30 上午10:52
 */
@RestController
@RequestMapping("/singer")
public class SingerController {


    @Autowired
    private SingerService singerService;

    @Autowired
    private ResultMap resultMap;


    /**
     * 默认用户头像
     */
    private static final String defaultSingerPic = "/img/singerPic/hhh.jpg";

    /**
     * 模糊及分页查询
     *
     * @param queryInfo
     * @return
     */
    @PostMapping("/get")
    public PageInfo<Singer> getAllSingers(@RequestBody QueryInfo queryInfo) {
        PageInfo<Singer> pageInfo = singerService.selectList(queryInfo);
        return pageInfo;
    }

    /**
     * 查询一个歌手
     *
     * @param id
     * @return
     */
    @GetMapping("/getOne/{id}")
    public ResultMap getOneSinger(@PathVariable Integer id) {
        Singer singer = singerService.selectById(id);
        if (singer != null) {                           //将singer对象放进res.data.message
            return resultMap.success().code(200).message(singer);
        }
        return resultMap.fail().code(401).message("歌手查找失败");
    }

    /**
     * 添加一个歌手
     *
     * @param singer
     * @return
     */
    @PostMapping("/add")
    public ResultMap addSinger(@RequestBody Singer singer) {
        singer.setPic(defaultSingerPic);// 设置默认歌手图片
        int insert = singerService.insert(singer);
        if (insert > 0) {
            return resultMap.success().code(200).message("歌手" + singer.getName() + "添加成功");
        }
        return resultMap.fail().code(401).message("歌手" + singer.getName() + "添加失败");
    }

    /**
     * 更新一个歌手
     *
     * @param singer
     * @return
     */
    @PutMapping("/update")
    public ResultMap updateSinger(@RequestBody Singer singer) {
        int update = singerService.updateById(singer);
        if (update > 0) {
            return resultMap.success().code(200).message("歌手" + singer.getName() + "更新成功");
        }
        return resultMap.fail().code(401).message("歌手" + singer.getName() + "更新失败");
    }

    /**
     * 删除一个歌手
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public ResultMap deleteSinger(@PathVariable Integer id) {
        Singer singer = singerService.selectById(id);
        deleteSingerPicFile(singer); // 删除歌手图片文件

        int delete = singerService.deleteById(id);
        if (delete > 0) {
            return resultMap.success().code(200).message("歌手[ " + singer.getName() + " ]删除成功");
        }
        return resultMap.fail().code(401).message("歌手[ " + singer.getName() + " ]删除失败");
    }

    /**
     * 更新歌手的图片
     *
     * @param multipartFile
     * @param id
     * @return
     */
    @PostMapping("/updateSingerPic/{id}")//restful风格
    public ResultMap updateSingerPic(@RequestParam("file") MultipartFile multipartFile, @PathVariable int id) {
        //上传图片文件
        if (multipartFile.isEmpty()) {
            return resultMap.fail().code(401).message("图片上传失败");
        }
        if (!multipartFile.getContentType().equals("image/jpeg")) {
            return resultMap.fail().code(401).message("上传的文件类型错误");
        }

        // 文件名 = 当前时间到毫秒 + 原来的文件名
        String fileName = System.currentTimeMillis() + multipartFile.getOriginalFilename(); // 防止文件名重复
        // 文件路径=music-server/img/singerPic/
        String filePath = System.getProperty("user.dir")  // music-server
                + System.getProperty("file.separator")    // /
                + "img"                                   // img
                + System.getProperty("file.separator")    // /
                + "singerPic"                             // singerPic
                + System.getProperty("file.separator");   // /
        // 如果文件路径不存在，新增该路径
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdir();
        }
        // 实际的文件地址
        File dest = new File(filePath + fileName);
        // 存储到数据库里的相对文件地址
        String storeAvatorPath = "/img/singerPic/" + fileName;
        try {
            multipartFile.transferTo(dest);//上传
            Singer singer = singerService.selectById(id);
            deleteSingerPicFile(singer); //如果当前歌手的图片不是默认图片，更新时，删除旧图片

            singer.setPic(storeAvatorPath); //数据库更新新的图片的地址
            int update = singerService.updateById(singer);

            if (update > 0) {
                return resultMap.success().code(200).message("图片更新成功");
            }
            return resultMap.fail().code(401).message("图片更新失败");
        } catch (IOException e) {
            return resultMap.fail().code(401).message("图片更新失败");
        }
    }

    /**
     * 批量删除歌手
     *
     * @param params
     * @return
     */
    @PostMapping("/delSome")
    public ResultMap delSome(@RequestBody Map params) {
        List<Integer> list = (List<Integer>) params.get("ids");
        List<Singer> listSinger = singerService.getListSinger(list);
        listSinger.forEach(singer -> {
            deleteSingerPicFile(singer); // 删除歌手图片文件
        });

        int i = singerService.deleteSome(list);
        if (i > 0) {
            return resultMap.success().code(200).message("批量删除成功");
        }
        return resultMap.fail().code(401).message("批量删除失败");
    }

    /**
     * 删除歌手图片文件
     *
     * @param singer
     */
    private void deleteSingerPicFile(Singer singer) {
        // 歌手图片地址（图片地址不等于默认图片地址时删掉歌手图片）
        if (!singer.getPic().equals(defaultSingerPic)) {
            String songPicPath = System.getProperty("user.dir") + singer.getPic();
            File songPicFile = new File(songPicPath);
            if (songPicFile.exists()) {
                songPicFile.delete();
            }
        }
    }

}
