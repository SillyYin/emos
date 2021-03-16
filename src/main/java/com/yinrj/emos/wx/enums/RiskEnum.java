package com.yinrj.emos.wx.enums;

/**
 * @author yinrongjie
 * @version 1.0
 * @description
 * @date 2021/3/16
 */
public enum RiskEnum {
    LOW_RISK(1, "低风险"),
    MIDDLE_RIST(2, "中风险"),
    HIGH_RISK(3, "高风险");

    public int code;
    public String msg;
    RiskEnum(int code, String msg) {
    }
}
