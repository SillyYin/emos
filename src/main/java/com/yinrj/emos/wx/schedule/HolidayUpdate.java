package com.yinrj.emos.wx.schedule;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yinrj.emos.wx.db.dao.TbHolidaysDao;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yinrongjie
 * @version 1.0
 * @description 获取一年中所有的假期
 * @date 2021/3/11
 */
@Component
public class HolidayUpdate {
    private static final String URL = "https://api.apihubs.cn/holiday/get";
    private static final String HOLIDAY_FLAG = "2";
    private static final String SUCCESS_FLAG = "0";
    private static final Integer YEAR_DAY = 366;

    private final int year = DateUtil.thisYear();

    private final TbHolidaysDao holidaysDao;

    public HolidayUpdate(TbHolidaysDao holidaysDao) {
        this.holidaysDao = holidaysDao;
    }

    @Transactional
    @Scheduled(cron = "0 0 0 */1 * ?")
    public void update() throws ParseException {
        Date res = holidaysDao.selectOneData();
        if (res != null) {
            LocalDate date = Instant.ofEpochMilli(res.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            if (date.getYear() != year) {
                holidaysDao.deleteData();
                insertDate();
            }
        } else {
            insertDate();
        }
    }

    @Transactional
    public void insertDate() throws ParseException {
        JSONArray holidays = getHolidays();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        for (Object holiday : holidays) {
            holidaysDao.insertData(format.parse(String.valueOf(JSONUtil.parseObj(holiday).getObj("date"))));
        }
    }

    private JSONArray getHolidays() {
        Map<String, Object> map = new HashMap<>();
        map.put("year", year);
        map.put("workday", HOLIDAY_FLAG);
        map.put("size", YEAR_DAY);
        String object = HttpUtil.get(URL, map);
        JSONObject res = JSONUtil.parseObj(object);
        if (!SUCCESS_FLAG.equals(res.get("code"))) {
            throw new RuntimeException("获取假期时间失败");
        }
        return res.getJSONObject("data").getJSONArray("list");
    }
}
