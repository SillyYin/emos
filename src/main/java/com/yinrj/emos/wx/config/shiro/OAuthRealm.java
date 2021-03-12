package com.yinrj.emos.wx.config.shiro;

import com.yinrj.emos.wx.db.entity.TbUser;
import com.yinrj.emos.wx.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author yinrongjie
 * @version 1.0
 * @description 定义shiro框架的认证和授权方法
 * @date 2021/3/2
 */
@Component
public class OAuthRealm extends AuthorizingRealm {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof OAuth2Token;
    }

    /**
     * 用于校验授权
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        //TODO 查询用户的权限列表
        //TODO 把权限列表添加到info对象中
        return info;
    }

    /**
     * 认证，验证登录时使用
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        // 从令牌中获取userID，然后检测账户状态
        String token = (String) authenticationToken.getPrincipal();
        int userId = jwtUtil.getUserId(token);
        TbUser user = userService.searchById(userId);
        if (user == null) {
            throw new LockedAccountException("账号已经被锁定，请联系管理员");
        }
        // 往info中添加用户信息、Token字符串
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, token, getName());
        return info;
    }
}
