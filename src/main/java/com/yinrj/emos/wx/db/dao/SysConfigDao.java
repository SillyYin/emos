package com.yinrj.emos.wx.db.dao;

import com.yinrj.emos.wx.db.entity.SysConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysConfigDao {
    List<SysConfig> selectAllConfig();
}