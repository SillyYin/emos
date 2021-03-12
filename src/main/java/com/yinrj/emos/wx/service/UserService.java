package com.yinrj.emos.wx.service;

import com.yinrj.emos.wx.db.entity.TbUser;

import java.util.Set;

/**
 * @author yinrongjie
 * @version 1.0
 * @description
 * @date 2021/3/7
 */
public interface UserService {
    int registerUser(String registerCode, String code, String nickName, String photo);

    Set<String> searchUserPermission(int userId);

    Integer login(String code);

    TbUser searchById(int userId);
}
