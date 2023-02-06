package com.itmo.eva.model.vo.teacher;

import lombok.Data;

import java.io.Serializable;

@Data
public class TeacherNameVo implements Serializable {

    /**
     * 教师id
     */
    private Long id;

    /**
     * 教师姓名
     */
    private String name;

    private static final long serialVersionUID = 1L;

}
