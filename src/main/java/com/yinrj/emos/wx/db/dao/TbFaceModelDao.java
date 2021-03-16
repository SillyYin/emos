package com.yinrj.emos.wx.db.dao;

import com.yinrj.emos.wx.db.entity.TbFaceModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TbFaceModelDao {
    void insert(TbFaceModel faceModel);

    String searchFaceModel(int userId);

    void delete(int userId);
}