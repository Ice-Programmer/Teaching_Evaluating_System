package com.itmo.eva.model.dto.teacher;

import lombok.Data;

import java.io.Serializable;

/**
 * 添加教师
 */
@Data
public class TeacherAddRequest implements Serializable {

    /**
     * 教师名称
     */
    private String name;

    /**
     * 性别
     */
    private Integer sex;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 职称
     */
    private Integer position;

    /**
     * 职称
     */
    private Integer title;

    /**
     * 专业（0-计算机，1-自动化）
     */
    private Integer major;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 国籍（0-俄罗斯，1-中国）
     */
    private Integer identity;

    private static final long serialVersionUID = 1L;
}
