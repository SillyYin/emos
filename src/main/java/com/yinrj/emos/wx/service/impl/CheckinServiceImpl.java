package com.yinrj.emos.wx.service.impl;

import com.yinrj.emos.wx.constant.SystemContant;
import com.yinrj.emos.wx.db.dao.TbCheckinDao;
import com.yinrj.emos.wx.db.dao.TbHolidaysDao;
import com.yinrj.emos.wx.service.CheckinService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * @author yinrongjie
 * @version 1.0
 * @description
 * @date 2021/3/12
 */
@Slf4j
@Service
@Scope("prototype")
public class CheckinServiceImpl implements CheckinService {
    private final TbHolidaysDao holidaysDao;

    private final TbCheckinDao checkinDao;

    private final SystemContant contant;

    public CheckinServiceImpl(TbHolidaysDao holidaysDao, TbCheckinDao checkinDao, SystemContant contant) {
        this.holidaysDao = holidaysDao;
        this.checkinDao = checkinDao;
        this.contant = contant;
    }


    @Override
    public String validCanCheckIn(int userId, String date) {
        Integer isHoliday = holidaysDao.searchTodayIsHoliday();
        if (isHoliday != null) {
            return "节假日不需要考勤";
        }

        String start = LocalDate.now().toString() + contant.attendanceStartTime;
        String end = LocalDate.now().toString() + contant.attendanceEndTime;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = LocalDateTime.parse(start);
        LocalDateTime endTime = LocalDateTime.parse(end);
        log.debug("now: {}; startTime: {}; endTime: {}", now, startTime, endTime);
        if (now.isBefore(startTime)) {
            return "没有到上班考勤开始时间";
        } else if (now.isAfter(endTime)) {
            return "超过了上班考勤结束时间";
        } else {
            HashMap<String, Object> map = new HashMap<>();
            map.put("userId", userId);
            map.put("date", date);
            map.put("start", start);
            map.put("end", end);
            return checkinDao.haveCheckin(map) != null ? "今日已经进行了考勤" : "可以考勤";
        }
    }
}
