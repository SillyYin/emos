package com.yinrj.emos.wx.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author yinrongjie
 * @version 1.0
 * @description
 * @date 2021/2/28
 */
@ApiModel
@Data
public class SayHelloDto {
    @NotBlank
    @ApiModelProperty("姓名")
    private String name;
}
