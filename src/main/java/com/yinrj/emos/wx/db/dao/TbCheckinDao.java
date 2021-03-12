package com.yinrj.emos.wx.db.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;

@Mapper
public interface TbCheckinDao {
    Integer haveCheckin(HashMap<String, Object> map);
}