package com.yinrj.emos.wx.db.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.Date;

@Mapper
public interface TbHolidaysDao {
    int insertData(Date date);

    void deleteData();

    Date selectOneData();

    Integer searchTodayIsHoliday();
}