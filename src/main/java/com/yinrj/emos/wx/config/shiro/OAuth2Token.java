package com.yinrj.emos.wx.config.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author yinrongjie
 * @version 1.0
 * @description 把token封装成shiro需要的认证对象
 * @date 2021/3/2
 */
public class OAuth2Token implements AuthenticationToken {
    private String token;

    public OAuth2Token(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
