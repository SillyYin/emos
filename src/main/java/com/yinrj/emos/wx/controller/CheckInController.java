package com.yinrj.emos.wx.controller;

import com.yinrj.emos.wx.common.util.R;
import com.yinrj.emos.wx.config.shiro.JwtUtil;
import com.yinrj.emos.wx.service.CheckinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * @author yinrongjie
 * @version 1.0
 * @description
 * @date 2021/3/12
 */
@RestController
@RequestMapping("/checkin")
@Api("签到模块Web接口")
@Slf4j
public class CheckInController {
    private final CheckinService checkinService;

    private final JwtUtil jwtUtil;

    public CheckInController(CheckinService checkinService, JwtUtil jwtUtil) {
        this.checkinService = checkinService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/validate")
    @ApiOperation("查看今天是否可以签到")
    public R validateCanCheckIn(@RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);
        String result = checkinService.validCanCheckIn(userId, LocalDate.now().toString());
        return R.ok().put("result", result);
    }
}
