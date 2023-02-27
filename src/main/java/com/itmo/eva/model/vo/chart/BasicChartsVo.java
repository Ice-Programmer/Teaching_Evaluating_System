package com.itmo.eva.model.vo.chart;

import com.itmo.eva.model.vo.teacher.TeacherGenderVo;
import com.itmo.eva.model.vo.teacher.TeacherScoreVo;
import com.itmo.eva.model.vo.teacher.TeacherTitleVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BasicChartsVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 俄方教师性别统计
     */
    private TeacherGenderVo RussianTeacherGenderChart;

    /**
     * 俄方教师职位统计
     */
    private TeacherTitleVo RussianTeacherTitleChart;

    /**
     * 中方教师性别统计
     */
    private TeacherGenderVo ChineseTeacherGenderChart;

    /**
     * 中方教师职位统计
     */
    private TeacherTitleVo ChineseTeacherTitleChart;

    /**
     * 俄方分数前十排名
     */
    private List<TeacherScoreVo> RussianTeacherScoreChart;


}
