package com.kai.controller;

import com.github.pagehelper.PageInfo;
import com.kai.entity.QueryInfo;
import com.kai.entity.User;
import com.kai.result.ResultMap;
import com.kai.service.UserService;
import com.kai.shiro.jwt.JwtToken;
import com.kai.utils.JwtUtil;
import com.kai.utils.VerifyCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kai
 * @date 2021/7/24 下午2:07
 * 用来处理身份认证
 */
@RequestMapping("/user")
@RestController
@CrossOrigin //允许跨域 前后端分离
@Slf4j
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    private ResultMap resultMap;


    /**
     * 默认用户头像
     */
    private static final String defaultUserAvator = "/avatorImages/default.jpg";


    /**
     * 模糊及分页查询
     *
     * @param queryInfo
     * @return
     */
    @PostMapping("/get")
    public PageInfo<User> getAllUsers(@RequestBody QueryInfo queryInfo) {
        PageInfo<User> pageInfo = userService.selectList(queryInfo);
        return pageInfo;
    }

    /**
     * 查询一个用户
     *
     * @param id
     * @return
     */
    @GetMapping("/getOne/{id}")
    public ResultMap getOneUser(@PathVariable Integer id) {
        User user = userService.selectById(id);
        if (user != null) {
            return resultMap.success().code(200).message(user);
        }
        return resultMap.fail().code(401).message("用户查找失败");
    }

    /**
     * 更新一个用户
     *
     * @param user
     * @return
     */
    @PutMapping("/update")
    public ResultMap updateUser(@RequestBody User user) {
        int update = userService.updateById(user);
        if (update > 0) {
            return resultMap.success().code(200).message("用户" + user.getUsername() + "更新成功");
        }
        return resultMap.fail().code(401).message("用户" + user.getUsername() + "更新失败");
    }

    /**
     * 生成验证码图片
     */
    @GetMapping("getImage")
    public String getImageCode(HttpServletRequest request) throws IOException {
        //1.使用工具类生成验证码
        String code = VerifyCodeUtils.generateVerifyCode(4);
        //2.将验证码放入servletContext作用域
        request.getServletContext().setAttribute("code", code);
        //3.将图片转为base64
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        VerifyCodeUtils.outputImage(150, 40, byteArrayOutputStream, code);
        return "data:image/png;base64," + Base64Utils.encodeToString(byteArrayOutputStream.toByteArray());
    }


    /*
    登录
     */
    @RequestMapping("/login")
    public ResultMap login(@RequestBody User loginuser, String code, HttpServletRequest request) {
//        比较验证码
        String codes = (String) request.getServletContext().getAttribute("code");

        log.info("用户输入的验证码信息:[{}]", code);
        log.info("系统的验证码信息:[{}]", codes);

        if (codes.equalsIgnoreCase(code)) {
            try {
                String username = loginuser.getUsername();
                String password = loginuser.getPassword();

                //    获取主体对象：事先定义好 ShiroConfig类(整合shiro框架相关的配置类)
                User user = userService.selectOne(username);
                if (user == null) {
                    return resultMap.fail().code(401).message("用户名错误");
                } else {
                    HashMap<String, Object> map = new HashMap<>();

                    //盐 + 输入的密码(注意不是用户的正确密码) + 1024次散列，作为token生成的密钥
                    String salt = user.getSalt();
                    Md5Hash md5Hash = new Md5Hash(password, salt, 1024);
                    String secret = md5Hash.toHex();//toHex转换成16进制，32为字符
                    //生成token字符串                    username、secret
                    String token = JwtUtil.getJwtToken(username, secret);
                    JwtToken jwtToken = new JwtToken(token);        //转换成jwtToken（才可以被shiro识别）

                    //拿到Subject对象
                    Subject subject = SecurityUtils.getSubject();

                    //进行认证
                    subject.login(jwtToken);
                    resultMap.success().message("登陆成功").data(user).token(token);
                }
            } catch (IncorrectCredentialsException e) {
                resultMap.fail().message("密码错误");
            } catch (ExpiredCredentialsException e) {
                resultMap.fail().message("token过期，请重新登录");
            }
        } else {
            resultMap.fail().message("验证码错误！请重新输入！");
        }
        return resultMap;
    }

    /*
    注册操作
     */
    @RequestMapping("/register")
    public ResultMap insert(@RequestBody User user) {
        log.info("用户信息:[{}]", user.toString());

        try {
            user.setPic(defaultUserAvator);
            userService.insert(user);
            resultMap.success().message("注册成功，跳转到登录页面进行登录认证！").data(user);
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.fail().message("注册失败，请重新注册！");

        }

        return resultMap;

    }


    /**
     * 更新用户的图片
     *
     * @param multipartFile
     * @param id
     * @return
     */
    @PostMapping("/updateUserAvator/{id}")
    public ResultMap updateUserAvator(@RequestParam("file") MultipartFile multipartFile, @PathVariable int id) {
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
        String filePath = System.getProperty("user.dir") + System.getProperty("file.separator") + "avatorImages"
                + System.getProperty("file.separator");
        // 如果文件路径不存在，新增该路径
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdir();
        }
        // 实际的文件地址
        File dest = new File(filePath + fileName);
        // 存储到数据库里的相对文件地址
        String storeAvatorPath = "/avatorImages/" + fileName;
        try {
            multipartFile.transferTo(dest);
            User user = userService.selectById(id);
            deleteUserAvatorFile(user); //如果当前用户的图片不是默认图片，更新时，删除旧图片

            user.setPic(storeAvatorPath); //数据库更新新的图片的地址
            int update = userService.updateById(user);
            if (update > 0) {
                return resultMap.success().code(200).message("图片更新成功");
            }
            return resultMap.fail().code(401).message("图片更新失败");
        } catch (IOException e) {
            return resultMap.fail().code(401).message("图片更新失败");
        }
    }

    /**
     * 批量删除用户
     *
     * @param params
     * @return
     */
    @PostMapping("/delSome")
    public ResultMap delSome(@RequestBody Map params) {
        List<Integer> list = (List<Integer>) params.get("ids");
        List<User> listUser = userService.getListUser(list);

//        遍历删除用户图片
        listUser.forEach(user -> {
            deleteUserAvatorFile(user); // 删除用户图片文件
        });

        int i = userService.deleteSome(list);
        if (i > 0) {
            return resultMap.success().code(200).message("批量删除成功");
        }
        return resultMap.fail().code(401).message("批量删除失败");
    }

    /**
     * 删除用户图片文件
     *
     * @param user
     */
    private void deleteUserAvatorFile(User user) {
        // 用户图片地址（图片地址不等于默认图片地址时删掉用户图片）
        if (!user.getPic().equals(defaultUserAvator)) {
            String songAvatorPath = System.getProperty("user.dir") + user.getPic();
            File songAvatorFile = new File(songAvatorPath);
            if (songAvatorFile.exists()) {
                songAvatorFile.delete();
            }
        }
    }

    /**
     * 删除一个用户
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public ResultMap deleteUser(@PathVariable Integer id) {
        User user = userService.selectById(id);
        deleteUserAvatorFile(user); // 删除用户图片文件

        int delete = userService.deleteById(id);
        if (delete > 0) {
            return resultMap.success().code(200).message("用户[ " + user.getUsername() + " ]删除成功");
        }
        return resultMap.fail().code(401).message("用户[ " + user.getUsername() + " ]删除失败");
    }

    /*
 退出
  */
    @RequestMapping("/logout")
//    @CacheEvict(value = "allUsers", allEntries = true)
    @Caching(evict = {@CacheEvict(value = "allUsers", allEntries = true),
            @CacheEvict(value = "allInfomations", allEntries = true),
            @CacheEvict(value = "allSingers", allEntries = true),
            @CacheEvict(value = "allSongLists", allEntries = true)
    })
    public ResultMap logout() {
        try {
            Subject subject = SecurityUtils.getSubject();
            subject.logout();
            resultMap.success();
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.fail();
        }
        return resultMap;
    }

}
