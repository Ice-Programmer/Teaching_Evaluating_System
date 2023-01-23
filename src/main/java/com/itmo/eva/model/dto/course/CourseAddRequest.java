package com.itmo.eva.model.dto.course;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 课程表
 * @TableName e_course
 */
@Data
public class CourseAddRequest implements Serializable {

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

    private static final long serialVersionUID = 1L;
}