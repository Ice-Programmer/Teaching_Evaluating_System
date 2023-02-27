package com.itmo.eva.model.vo.chart;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TeacherRankChartVo implements Serializable {

    /**
     * 教师名称
     */
    private String teacherName;

    /**
     * 分数
     */
    private Double score;

    /**
     * 教师一级指标分数
     */
    List<ScoreVo> systemScoreList;

    private static final long serialVersionUID = 1L;

}
