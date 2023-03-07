package com.itmo.eva.model.vo.course;

import lombok.Data;

import java.io.Serializable;

/**
 * 课程教师展示
 */
@Data
public class CourseTeacherVo implements Serializable {

    /**
     * 教师id
     */
    private Long tid;

    /**
     * 教师名称
     */
    private String teacherName;
}
