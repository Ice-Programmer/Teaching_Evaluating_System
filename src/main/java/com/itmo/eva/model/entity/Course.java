package com.itmo.eva.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 课程表
 * @TableName e_course
 */
@TableName(value ="e_course")
@Data
public class Course implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 课程中文名
     */
    private String cName;

    /**
     * 课程中文名
     */
    private String eName;

    /**
     * 专业
     */
    private Integer major;

    /**
     * 教师id
     */
    private Long tid;

    /**
     * 年级
     */
    private Integer grade;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}