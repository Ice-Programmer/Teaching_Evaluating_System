package com.itmo.eva.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 课程表
 * @TableName e_course
 */
@Data
public class CourseVo implements Serializable {
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
     * 教师名称
     */
    private String teacher;

    /**
     * 年级
     */
    private Integer grade;

    private static final long serialVersionUID = 1L;
}