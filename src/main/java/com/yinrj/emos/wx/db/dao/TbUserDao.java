package com.yinrj.emos.wx.db.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.Set;

@Mapper
public interface TbUserDao {
    /**
     * 判断是否存在root超级管理员用户
     * @return
     */
    boolean haveRootUser();

    int insert(HashMap<String, Object> param);

    Integer searchIdByOpenId(String openId);

    Set<String> searchUserPermissions(int userId);
}