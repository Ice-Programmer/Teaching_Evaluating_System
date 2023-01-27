package com.itmo.eva.model.vo.Evaluation;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 评测完成情况学生基本信息
 */
@Data
public class StudentEvaVo implements Serializable {

    /**
     * 班级号
     */
    private String studentClass;

    /**
     * 姓名
     */
    private String name;

    /**
     * 学号
     */
    private String studentId;
}
