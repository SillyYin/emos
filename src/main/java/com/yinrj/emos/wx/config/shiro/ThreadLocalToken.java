package com.yinrj.emos.wx.config.shiro;

import org.springframework.stereotype.Component;

/**
 * @author yinrongjie
 * @version 1.0
 * @description 媒介类，用于传递token，ThreadLocal每个线程私有
 * @date 2021/3/3
 */
@Component
public class ThreadLocalToken {
    private final ThreadLocal<String> local = new ThreadLocal<>();

    public void setToken(String token) {
        local.set(token);
    }

    public String getToken() {
        return local.get();
    }

    public void clear() {
        local.remove();
    }
}
