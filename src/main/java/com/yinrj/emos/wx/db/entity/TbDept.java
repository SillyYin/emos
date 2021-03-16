package com.yinrj.emos.wx.db.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * tb_dept
 * @author 
 */
@Data
public class TbDept implements Serializable {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 部门名称
     */
    private String deptName;

    private static final long serialVersionUID = 1L;
}