package com.yinrj.emos.wx.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yinrongjie
 * @version 1.0
 * @description 自定义异常类
 * @date 2021/2/28
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class EmosException extends RuntimeException{
    private String msg;
    private int code = 500;

    public EmosException(String message) {
        super(message);
        this.msg = message;
    }

    public EmosException(String message, Throwable cause) {
        super(message, cause);
        this.msg = message;
    }

    public EmosException(String message, int code) {
        super(message);
        this.msg = message;
        this.code = code;
    }

    public EmosException(String message, Throwable cause, int code) {
        super(message, cause);
        this.msg = message;
        this.code = code;
    }
}
