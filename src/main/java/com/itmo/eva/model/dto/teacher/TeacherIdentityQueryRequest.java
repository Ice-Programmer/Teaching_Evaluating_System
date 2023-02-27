package com.itmo.eva.model.dto.teacher;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询教师国籍参数
 */
@Data
public class TeacherIdentityQueryRequest implements Serializable {

    /**
     * 国籍
     */
    private Integer identity;

    private static final long serialVersionUID = 1L;

}
