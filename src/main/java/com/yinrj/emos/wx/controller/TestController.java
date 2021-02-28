package com.yinrj.emos.wx.controller;

import com.yinrj.emos.wx.common.util.R;
import com.yinrj.emos.wx.dto.SayHelloDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    @PostMapping("/test")
    public R sayHello(@Valid @RequestBody SayHelloDto sayHelloDto) {
        return R.ok().put("message", "Hello, " + sayHelloDto.getName());
    }
}
