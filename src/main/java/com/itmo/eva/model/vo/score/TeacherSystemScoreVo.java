package com.itmo.eva.model.vo.score;

import com.itmo.eva.model.vo.system.SecondSystemScoreVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TeacherSystemScoreVo implements Serializable {

    /**
     * 教师名称
     */
    private String name;

    /**
     * 二级指标平均分
     */
    private List<SecondSystemScoreVo> scoreList;

    /**
     * 二级评价总分
     */
    private Double totalScore;

    private static final long serialVersionUID = 1L;
}
