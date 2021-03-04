package com.yinrj.emos.wx.aop;

import com.yinrj.emos.wx.common.util.R;
import com.yinrj.emos.wx.config.shiro.ThreadLocalToken;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author yinrongjie
 * @version 1.0
 * @description
 * @date 2021/3/4
 */
@Component
@Aspect
public class TokenAop {
    private final ThreadLocalToken token;

    public TokenAop(ThreadLocalToken token) {
        this.token = token;
    }

    @Pointcut("execution(public * com.yinrj.emos.wx.controller.*.*(..)))")
    public void aspect() {

    }

    @Around("aspect()")
    public Object round(ProceedingJoinPoint joinPoint) throws Throwable {
        R r = (R) joinPoint.proceed();
        String tToken = token.getToken();
        if (tToken != null) {
            r.put("token", token);
            token.clear();
        }
        return r;
    }
}
