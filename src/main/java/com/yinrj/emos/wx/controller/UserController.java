package com.yinrj.emos.wx.controller;

import com.yinrj.emos.wx.common.util.R;
import com.yinrj.emos.wx.config.shiro.JwtUtil;
import com.yinrj.emos.wx.dto.LoginDto;
import com.yinrj.emos.wx.dto.RegisterDto;
import com.yinrj.emos.wx.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author yinrongjie
 * @version 1.0
 * @description
 * @date 2021/3/7
 */
@RestController
@RequestMapping("/user")
@Api
public class UserController {
    private final UserService userService;

    private final JwtUtil jwtUtil;

    private final RedisTemplate redisTemplate;

    @Value("${emos.jwt.cache-expire}")
    private int cacheExpireTime;

    public UserController(UserService userService, JwtUtil jwtUtil, RedisTemplate redisTemplate) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    private void cacheToken(String token, int userId) {
        redisTemplate.opsForValue().set(token, String.valueOf(userId), cacheExpireTime, TimeUnit.DAYS);
    }

    @PostMapping("/register")
    @ApiOperation("注册用户")
    public R register(@Valid @RequestBody RegisterDto registerDto) {
        int userId = userService.registerUser(registerDto.getRegisterCode(), registerDto.getCode(), registerDto.getNickname(), registerDto.getPhoto());
        String token = jwtUtil.createToken(userId);
        cacheToken(token, userId);
        Set<String> permissionSet = userService.searchUserPermission(userId);
        return Objects.requireNonNull(R.ok("注册成功").put("token", token)).put("permission", permissionSet);
    }

    @PostMapping("/login")
    @ApiOperation("用户登陆")
    public R login(@Valid @RequestBody LoginDto loginDto) {
        int id = userService.login(loginDto.getCode());
        String token = jwtUtil.createToken(id);
        Set<String> permissionSet = userService.searchUserPermission(id);
        cacheToken(token, id);
        return Objects.requireNonNull(R.ok("登陆成功").put("token", token)).put("permission", permissionSet);
    }
}
