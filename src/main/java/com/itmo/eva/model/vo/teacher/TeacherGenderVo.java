package com.itmo.eva.model.vo.teacher;

import lombok.Data;

import java.io.Serializable;

/**
 * 教师性别基本信息
 */
@Data
public class TeacherGenderVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long maleNum;

    private Long femaleNum;

}
