package com.itmo.eva.model.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 二级指标
 */
@Data
public class SecondSystem implements Serializable {

    /**
     * 评价id
     */
    private Integer eid;

    /**
     * 学生id
     */
    private Integer studentId;

    /**
     * 教师id
     */
    private Integer tid;

    /**
     * 课程id
     */
    private Integer cid;

    /**
     * 二级指标id
     */
    private Integer secondId;

    /**
     * 二级指标分数
     */
    private Integer score;

    /**
     * 数据表名称
     */
    private String TableName;
}
