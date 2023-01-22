package com.itmo.eva.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 教师表
 * @TableName e_teacher
 */
@TableName(value ="e_teacher")
@Data
public class Teacher implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 教师名称
     */
    private String name;

    /**
     * 性别
     */
    private Integer sex;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 职称
     */
    private Integer position;

    /**
     * 职称
     */
    private Integer title;

    /**
     * 专业（0-计算机，1-自动化）
     */
    private Integer major;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 国籍（0-俄罗斯，1-中国）
     */
    private Integer identity;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}