package com.kai.shiro.realm;
/**
 * MyRealm[shiro两大功能]：认证和授权
 * 只要调用了subject.login(token)方法，就会进入到realm的doGetAuthenticationInfo 执行这个认证方法
 * shiro使用的token和客户端保存的token都是jwt生成的。所以下面两种情况都会调用到subject.login方法进入到realm中：
 * 在认证时（登录controller中调用）
 * 认证通过后每次校验token正确性时（在JwtFilter中调用）
 */

import com.kai.entity.User;
import com.kai.service.UserService;
import com.kai.shiro.jwt.JwtToken;
import com.kai.utils.JwtUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

/**
 * @author: zhk
 * 自定义Realm
 */
public class MyRealm extends AuthorizingRealm {
    @Autowired
    @Lazy
    private UserService userService;

    /**
     * 限定这个realm只能处理JwtToken（不加的话会报错）
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }


    /**
     * 认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) {
        String token = (String) auth.getCredentials();  //JwtToken中重写了这个方法了
        String username = JwtUtil.getUsername(token);   // 获得username

        //用户不存在（这个在登录时不会进入，只有在token校验时才有可能进入）
        if (username == null)
            throw new UnknownAccountException();

        //根据用户名，查询数据库获取到正确的用户信息
        User user = userService.selectOne(username);

        //用户不存在（这个在登录时不会进入，只有在token校验时才有可能进入）
        if (user == null)
            throw new UnknownAccountException();

        //密码错误(这里获取到password，就是3件套处理后的保存到数据库中的凭证，作为密钥)
        if (!JwtUtil.verifyToken(token, username, user.getPassword())) {
            throw new IncorrectCredentialsException();
        }
        //toke过期
        if (JwtUtil.isExpire(token)) {
            throw new ExpiredCredentialsException();
        }

        return new SimpleAuthenticationInfo(user, token, getName());
    }

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
//
////        获取身份信息 primaryPrincipal=username
//        String primaryPrincipal = (String) principals.getPrimaryPrincipal();
//        System.out.println("调用授权验证：" + primaryPrincipal);
//
////        在工厂中获取service对象
//        UserService userService = (UserService) ApplicationContextUtils.getBean("userService");
//
////        用service对象  根据主身份信息     获取角色 和 授权信息
//
////        获取角色信息
//        User user = userService.findRolesByUserName(primaryPrincipal);
//        if (!CollectionUtils.isEmpty(user.getRoles())) {
//            SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
//            user.getRoles().forEach(role -> {
//                simpleAuthorizationInfo.addRole(role.getName());
//
////        获取角色信息之后  根据角色role的id  获取角色的权限信息
//                List<Perms> perms = userService.findPermsByRoleId(role.getId());
//                if (!CollectionUtils.isEmpty(perms)) {
//                    perms.forEach(perm -> {
//                        simpleAuthorizationInfo.addStringPermission(perm.getName());
//                    });
//                }
//
//
//            });
//
//            return simpleAuthorizationInfo;
//        }
//
        return null;

    }
}

