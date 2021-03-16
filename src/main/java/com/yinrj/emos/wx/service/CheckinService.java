package com.yinrj.emos.wx.service;

import java.util.HashMap;

/**
 * @author yinrongjie
 * @version 1.0
 * @description
 * @date 2021/3/12
 */
public interface CheckinService {
    String validCanCheckIn(int userId, String date);

    void checkin(HashMap<String, Object> map);
}
