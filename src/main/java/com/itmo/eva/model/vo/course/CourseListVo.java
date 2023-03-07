package com.itmo.eva.model.vo.course;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 课程列表展示
 */
@Data
public class CourseListVo implements Serializable {

    /**
     * 课程id
     */
    private Integer id;

    /**
     * 课程中文名
     */
    private String cName;

    /**
     * 课程英文名
     */
    private String eName;

    /**
     * 专业
     */
    private Integer major;

    /**
     * 年级
     */
    private String grade;

    /**
     * 教师信息
     */
    private List<CourseTeacherVo> teacherList;

    private static final long serialVersionUID = 1L;

}
