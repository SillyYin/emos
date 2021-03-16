package com.yinrj.emos.wx.db.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TbCityDao {
    String searchByCityName(String city);
}