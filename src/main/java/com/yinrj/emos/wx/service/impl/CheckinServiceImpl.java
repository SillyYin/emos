package com.yinrj.emos.wx.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.yinrj.emos.wx.constant.SystemContant;
import com.yinrj.emos.wx.db.dao.*;
import com.yinrj.emos.wx.db.entity.TbCheckin;
import com.yinrj.emos.wx.db.entity.TbFaceModel;
import com.yinrj.emos.wx.enums.CheckinStatusEnum;
import com.yinrj.emos.wx.enums.RiskEnum;
import com.yinrj.emos.wx.exception.EmosException;
import com.yinrj.emos.wx.service.CheckinService;
import com.yinrj.emos.wx.task.EmailTask;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    @Value("${emos.face.createFaceModelUrl}")
    private String createFaceModelUrl;

    @Value("${emos.face.checkinUrl}")
    private String checkinUrl;

    @Value("${emos.email.hr}")
    private String hrEmail;

    @Value("${emos.code}")
    private String code;

    private final EmailTask emailTask;

    private final TbHolidaysDao holidaysDao;

    private final TbCheckinDao checkinDao;

    private final TbUserDao userDao;

    private final SystemContant contant;

    private final TbFaceModelDao faceModelDao;

    private final TbCityDao cityDao;

    public CheckinServiceImpl(TbHolidaysDao holidaysDao, TbCheckinDao checkinDao, SystemContant contant,
                              TbFaceModelDao faceModelDao, TbCityDao cityDao, EmailTask emailTask, TbUserDao userDao) {
        this.holidaysDao = holidaysDao;
        this.checkinDao = checkinDao;
        this.contant = contant;
        this.faceModelDao = faceModelDao;
        this.cityDao = cityDao;
        this.emailTask = emailTask;
        this.userDao = userDao;
    }


    @Override
    public String validCanCheckIn(int userId, String date) {
        Integer isHoliday = holidaysDao.searchTodayIsHoliday();
        if (isHoliday != null) {
            return "节假日不需要考勤";
        }

        CheckinStatusEnum checkinStatusEnum = getCheckinStatus();
        if (checkinStatusEnum.code == 0 || checkinStatusEnum.code == 3) {
            return checkinStatusEnum.msg;
        } else {
            String start = LocalDate.now().toString() + " " + contant.attendanceStartTime;
            String end = LocalDate.now().toString() + " " + contant.attendanceEndTime;
            HashMap<String, Object> map = new HashMap<>();
            map.put("userId", userId);
            map.put("date", date);
            map.put("start", start);
            map.put("end", end);
            return checkinDao.haveCheckin(map) != null ? "今日已经进行了考勤" : "可以考勤";
        }
    }

    @Override
    public void checkin(HashMap<String, Object> map) {
        int status = getCheckinStatus().code;
        int userId = (Integer)map.get("userId");
        String faceModel = faceModelDao.searchFaceModel(userId);
        if (faceModel == null) {
            throw new EmosException("不存在人脸模型");
        } else {
            String path = (String) map.get("path");
            HttpRequest request = HttpUtil.createPost(checkinUrl);
            request.form("photo", FileUtil.file(path), "targetModel", faceModel);
            request.form("code", code);
            HttpResponse response = request.execute();
            if (response.getStatus() != 200) {
                log.error("人脸识别服务异常");
                throw new EmosException("人脸识别服务异常");
            }
            String body = response.body();
            if ("无法识别出人脸".equals(body) || "照片中存在多张人脸".equals(body)) {
                throw new EmosException(body);
            } else if ("False".equals(body)) {
                throw new EmosException("签到无效");
            } else if ("True".equals(body)) {
                int risk = RiskEnum.LOW_RISK.code;
                String city = (String) map.get("city");
                String district = (String) map.get("district");
                String address = (String) map.get("address");
                if (!StrUtil.isBlank(city) && !StrUtil.isBlank(district)) {
                    String cityCode = cityDao.searchByCityName(city);
                    try {
                        String url = "http://m." + cityCode + ".bendibao.com/news/yqdengji/?qu=" + district;
                        Document document = Jsoup.connect(url).get();
                        Elements elements = document.getElementsByClass("list-content");
                        if (elements.size() > 0) {
                            Element element = elements.get(0);
                            String result = element.select("p:last-child").text();
                            if (RiskEnum.HIGH_RISK.msg.equals(result)) {
                                risk = RiskEnum.HIGH_RISK.code;
                                HashMap<String, Object> userDept = userDao.searchNameAndDept(userId);
                                String name = (String) userDept.get("name");
                                String deptName = (String) userDept.get("dept_name");

                                deptName = deptName != null ? deptName : "";
                                SimpleMailMessage message = new SimpleMailMessage();
                                message.setTo(hrEmail);
                                message.setSubject("员工" + name + "身处高风险疫情地区警告");
                                message.setText(deptName + "员工" + name + "，" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")) + "处于" + address + "，属于新冠疫情高风险地区，请及时与该员工联系，核实情况！");
                                emailTask.sendAsync(message);


                            } else if (RiskEnum.MIDDLE_RIST.msg.equals(result)) {
                                risk = RiskEnum.MIDDLE_RIST.code;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new EmosException("风险等级获取失败");
                    }
                }
                String country = (String) map.get("country");
                String province = (String) map.get("province");
                TbCheckin entity = new TbCheckin();
                entity.setAddress(address);
                entity.setUserId(userId);
                entity.setCountry(country);
                entity.setProvince(province);
                entity.setDistrict(district);
                entity.setStatus((byte) status);
                entity.setRisk(risk);
                checkinDao.insert(entity);
            }
        }
    }

    @Override
    public void createFaceModel(int userId, String path) {
        HttpRequest request = HttpUtil.createPost(createFaceModelUrl);
        request.form("photo", FileUtil.file(path));
        request.form("code", code);
        HttpResponse response = request.execute();
        String body = response.body();
        if ("无法识别出人脸".equals(body) || "照片中存在多张人脸".equals(body)) {
            throw new EmosException(body);
        } else {
            TbFaceModel entity = new TbFaceModel();
            entity.setUserId(userId);
            entity.setFaceModel(body);
            faceModelDao.insert(entity);
        }
    }

    private CheckinStatusEnum getCheckinStatus() {
        String start = LocalDate.now().toString() + " " + contant.attendanceStartTime;
        String end = LocalDate.now().toString() + " " + contant.attendanceEndTime;
        String work = LocalDate.now().toString() + " " + contant.attendanceTime;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        LocalDateTime endTime = LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        LocalDateTime workTime = LocalDateTime.parse(work, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        if (now.isBefore(startTime)) {
            return CheckinStatusEnum.BEFORE_START;
        } else if (now.isAfter(endTime)) {
            return CheckinStatusEnum.AFTER_END;
        } else if (now.isBefore(workTime)) {
            return CheckinStatusEnum.NOT_LATE;
        } else if (now.isAfter(workTime) && now.isBefore(endTime)) {
            return CheckinStatusEnum.LATE;
        }
        return CheckinStatusEnum.OTEHR;
    }
}
