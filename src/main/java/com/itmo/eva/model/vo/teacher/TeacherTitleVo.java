package com.itmo.eva.model.vo.teacher;

import lombok.Data;

import java.io.Serializable;

/**
 * 教师职位信息
 */
@Data
public class TeacherTitleVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 教授人数
     */
    private Long professorNum;

    /**
     * 副教授人数
     */
    private Long associateProfessorNum;

    /**
     * 讲师人数
     */
    private Long lecturerNum;

}
