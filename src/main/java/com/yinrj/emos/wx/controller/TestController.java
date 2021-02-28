package com.yinrj.emos.wx.controller;

import com.yinrj.emos.wx.common.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yinrongjie
 * @version 1.0
 * @description 测试controller
 * @date 2021/2/28
 */
@RestController
@RequestMapping("/test")
@Api("测试web接口")
public class TestController {
    @ApiOperation("测试")
    @GetMapping("/test")
    public R sayHello() {
        return R.ok().put("message", "HelloWorld");
    }
}
