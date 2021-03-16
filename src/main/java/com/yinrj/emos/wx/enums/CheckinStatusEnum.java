package com.yinrj.emos.wx.enums;

/**
 * @author yinrongjie
 * @version 1.0
 * @description
 * @date 2021/3/13
 */
public enum CheckinStatusEnum {
    BEFORE_START(0, "没有到上班考勤开始时间"),
    AFTER_END(3, "超过了上班考勤结束时间"),
    NOT_LATE(1, "正常打卡"),
    LATE(2, "迟到"),
    OTEHR(-1, "其他情况")
    ;

    public int code;
    public String msg;


    CheckinStatusEnum(int code, String msg) {
    }
}
