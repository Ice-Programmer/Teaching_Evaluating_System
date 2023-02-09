package com.itmo.eva.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 学生表
 * @TableName e_student
 */
@TableName(value ="e_student")
@Data
public class StudentVo implements Serializable {
    /**
     * 主键
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
     * 班级
     */
    private String cid;

    /**
     * 年级
     */
    private String grade;

    private static final long serialVersionUID = 1L;
}