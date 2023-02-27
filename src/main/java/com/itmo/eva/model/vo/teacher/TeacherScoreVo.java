package com.itmo.eva.model.vo.teacher;

import com.itmo.eva.model.vo.chart.ScoreVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TeacherScoreVo implements Serializable {

    /**
     * 教师名称
     */
    private String teacherName;

    /**
     * 教师总分
     */
    private Double score;

    /**
     * 一级评价分数
     */
    private List<ScoreVo> systemScore;

    private static final long serialVersionUID = 1L;

}
