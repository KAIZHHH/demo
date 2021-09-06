package com.kai.utils;
/**
 * 2、JwtUtil
 * ​ Jwt的自定义工具类，主要功能如下：
 * <p>
 * 生成符合Jwt机制的token字符串
 * 可以对token字符串进行校验
 * 获取token中的用户信息
 * 判断token是否过期
 */

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @author: zhk
 * jwt工具，用来生成、校验token以及提取token中的信息
 */
@Slf4j
public class JwtUtil {
    //指定一个token过期时间（毫秒）
    private static final long EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000;  //7天
    private static String sec;

    /**
     * 生成token
     */
    //注意这里的sercet不是密码，而是进行三件套（salt+MD5+1024Hash）处理密码后得到的凭证
    //这里为什么要这么做，在controller中进行说明
//                                        账号            密码
    public static String getJwtToken(String username, String secret) {
        sec = secret;
        JWTCreator.Builder builder = JWT.create();//创建JWT
//        设置过期时间、签名算法
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME); //现在时间+过期时间[7天]
        Algorithm algorithm = Algorithm.HMAC256(secret);    //使用密钥进行哈希
        String token = builder.withClaim("username", username)
                .withExpiresAt(date)  //过期时间
                .sign(algorithm);//签名算法
// 附带username信息的token
        return token;
    }

    /**
     * 校验token是否正确
     */
    public static boolean verifyToken(String token, String username, String secret) {
        try {
            //根据密钥生成JWT效验器
            Algorithm algorithm = Algorithm.HMAC256(secret);
//            根据token 得出 新的token 进行对比
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim("username", username)
                    .build();

            //效验TOKEN（其实也就是比较两个token是否相同）
            DecodedJWT jwt = verifier.verify(token);
            return true;
//
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * 调用UserController的login方法中subject.login(token)方法
     * 就会进入到realm的doGetAuthenticationInfo 执行这个认证方法
     * 在token中获取到username信息
     */
    public static String getUsername(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 判断是否过期
     */
    public static boolean isExpire(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getExpiresAt().getTime() < System.currentTimeMillis();
    }




}
