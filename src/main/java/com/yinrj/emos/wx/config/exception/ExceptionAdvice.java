package com.yinrj.emos.wx.config.exception;

import com.yinrj.emos.wx.exception.EmosException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthenticatedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

/**
 * @author yinrongjie
 * @version 1.0
 * @description
 * @date 2021/3/4
 */
@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String validExceptionHandle(Exception e) {
        log.error(e.getMessage());
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException exception = (MethodArgumentNotValidException) e;
            return Objects.requireNonNull(exception.getBindingResult().getFieldError()).getDefaultMessage();
        } else if (e instanceof EmosException) {
            EmosException exception = (EmosException) e;
            return exception.getMessage();
        } else if (e instanceof UnauthenticatedException){
            return "不具备相关权限";
        } else {
            return "内部异常";
        }
    }
}
