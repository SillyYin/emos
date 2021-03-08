package com.yinrj.emos.wx.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yinrj.emos.wx.db.dao.TbUserDao;
import com.yinrj.emos.wx.exception.EmosException;
import com.yinrj.emos.wx.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author yinrongjie
 * @version 1.0
 * @description 使用原型模式是因为要使用threadlocal来刷新token
 * @date 2021/3/7
 */
@Service
@Slf4j
@Scope("prototype")
public class UserServiceImpl implements UserService {

    private static final String GET_OPEN_ID_URL = "https://api.weixin.qq.com/sns/jscode2session";

    private static final String ROOT_REGISTER_CODE = "000000";

    @Value("${weixin.app-id}")
    private String appId;

    @Value("${weixin.app-secret}")
    private String appSecret;

    private final TbUserDao userDao;

    public UserServiceImpl(TbUserDao userDao) {
        this.userDao = userDao;
    }

    private String getOpenId(String code) {
        Map<String, Object> map = new HashMap<>();
        map.put("appid", appId);
        map.put("secret", appSecret);
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String response = HttpUtil.post(GET_OPEN_ID_URL, map);
        JSONObject jsonObject = JSONUtil.parseObj(response);
        String openId = jsonObject.getStr("openid");
        if (StrUtil.isBlank(openId)) {
            throw new RuntimeException("临时登陆凭证错误");
        }
        return openId;
    }

    @Override
    public int registerUser(String registerCode, String code, String nickName, String photo) {
        if (ROOT_REGISTER_CODE.equals(registerCode)) {
            boolean isExistRootUser = userDao.haveRootUser();
            if (!isExistRootUser) {
                String openId = getOpenId(code);
                HashMap<String, Object> map = new HashMap<>();
                map.put("openId", openId);
                map.put("nickName", nickName);
                map.put("photo", photo);
                map.put("role", "[0]");
                map.put("status", 1);
                map.put("createTime", new Date());
                map.put("root", true);
                userDao.insert(map);
                return userDao.searchIdByOpenId(openId);
            } else {
                throw new EmosException("无法绑定超级管理员账号");
            }
        }
        return 0;
    }

    @Override
    public Set<String> searchUserPermission(int userId) {
        return userDao.searchUserPermissions(userId);
    }

    @Override
    public Integer login(String code) {
        String openId = getOpenId(code);
        Integer id = userDao.searchIdByOpenId(openId);
        if (id == null) {
            throw new EmosException("账户不存在");
        }
        // TODO 从消息队列中获得消息
        return id;
    }
}
