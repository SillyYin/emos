package com.yinrj.emos.wx.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author yinrongjie
 * @version 1.0
 * @description
 * @date 2021/3/13
 */
@Data
@ApiModel
public class CheckinDto {
    private String address;
    private String country;
    private String province;
    private String city;
    private String district;
}
