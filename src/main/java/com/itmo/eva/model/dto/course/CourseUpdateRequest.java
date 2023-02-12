package com.itmo.eva.model.dto.course;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 课程表
 * @TableName e_course
 */
@Data
public class CourseUpdateRequest implements Serializable {

    /**
     * id主键
     */
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
    private List<Long> tid;

    /**
     * 年级
     */
    private Integer grade;

    private static final long serialVersionUID = 1L;
}