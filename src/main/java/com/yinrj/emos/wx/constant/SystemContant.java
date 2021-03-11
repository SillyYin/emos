package com.yinrj.emos.wx.constant;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author yinrongjie
 * @version 1.0
 * @description
 * @date 2021/3/11
 */
@Data
@Component
public class SystemContant {
    public String attendanceStartTime;
    public String attendanceTime;
    public String attendanceEndTime;
    public String closingStartTime;
    public String closingTime;
    public String closingEndTime;
}
