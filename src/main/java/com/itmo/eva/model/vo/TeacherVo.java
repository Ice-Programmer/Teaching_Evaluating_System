package com.itmo.eva.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class TeacherVo implements Serializable {
    /**
     * 主键
     */
    private Long id;

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
    private String position;

    /**
     * 职称
     */
    private String title;

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
