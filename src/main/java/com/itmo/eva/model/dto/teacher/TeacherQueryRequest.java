package com.itmo.eva.model.dto.teacher;

import lombok.Data;

import java.io.Serializable;

/**
 * 教师具体分数查询
 */
@Data
public class TeacherQueryRequest implements Serializable {

    /**
     * 评测id
     */
    private Integer eid;

    /**
     * 教师id
     */
    private Integer tid;

    private static final long serialVersionUID = 1L;

}
