package com.itmo.eva.model.vo.score;

import com.itmo.eva.model.vo.system.FirstSystemScoreVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 教师在所有评测中的所有平均分
 */
@Data
public class TeacherAllScoreVo implements Serializable {

    /**
     * 教师名称
     */
    private String name;

    /**
     * 一级指标平均分数细则
     */
    private List<FirstSystemScoreVo> scoreList;

    /**
     * 总分
     */
    private Double totalScore;

    private static final long serialVersionUID = 1L;
}
