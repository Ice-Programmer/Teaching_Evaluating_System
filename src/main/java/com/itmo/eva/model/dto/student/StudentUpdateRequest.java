package com.itmo.eva.model.dto.student;

import lombok.Data;

import java.io.Serializable;

/**
 * 学生表
 * @TableName e_student
 */
@Data
public class StudentUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 学号
     */
    private String sid;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别(0-女 1-男）
     */
    private Integer sex;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 专业（0-计算机，1-自动化）
     */
    private Integer major;

    /**
     * 班级id
     */
    private Integer cid;

    /**
     * 年级
     */
    private Integer grade;

    private static final long serialVersionUID = 1L;
}