package com.yinrj.emos.wx.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author yinrongjie
 * @version 1.0
 * @description
 * @date 2021/3/8
 */
@Data
@ApiModel
public class LoginDto {
    @NotBlank(message = "临时授权不能为空")
    private String code;
}
