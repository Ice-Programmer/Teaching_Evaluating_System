package com.itmo.eva.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 学生表
 * @TableName e_student
 */
@TableName(value ="e_student")
@Data
public class Student implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 学号
     */
    private String sid;

    /**
     * 密码
     */
    private String password;

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

    /**
     * 用户名
     */
    private String tag;

    /**
     * 最近登录的一次ip地址
     */
    private String addressIp;

    /**
     * 形式:中国-省份-城市
     */
    private String addressName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}