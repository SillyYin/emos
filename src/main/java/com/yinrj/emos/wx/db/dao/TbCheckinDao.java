package com.yinrj.emos.wx.db.dao;

import com.yinrj.emos.wx.db.entity.TbCheckin;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;

@Mapper
public interface TbCheckinDao {
    Integer haveCheckin(HashMap<String, Object> map);

    void insert(TbCheckin checkin);
}