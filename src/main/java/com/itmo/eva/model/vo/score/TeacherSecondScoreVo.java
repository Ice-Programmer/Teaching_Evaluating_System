package com.itmo.eva.model.vo.score;

import lombok.Data;

import java.io.Serializable;

/**
 * 二级评价细则
 */
@Data
public class TeacherSecondScoreVo implements Serializable {

    /**
     * 教师名称
     */
    private String name;

    /**
     * 二级指标名称
     */
    private String systemName;

    /**
     * 二级指标分数
     */
    private Double score;

    private static final long serialVersionUID = 1L;
}
