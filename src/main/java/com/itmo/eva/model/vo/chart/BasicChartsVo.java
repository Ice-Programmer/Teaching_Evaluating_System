package com.itmo.eva.model.vo.chart;

import com.itmo.eva.model.vo.teacher.TeacherGenderVo;
import com.itmo.eva.model.vo.teacher.TeacherScoreVo;
import com.itmo.eva.model.vo.teacher.TeacherTitleVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class BasicChartsVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 俄方教师性别统计
     */
    private Map<String, Long> RussianTeacherGenderChart;

    /**
     * 俄方教师职位统计
     */
    private Map<String, Long> RussianTeacherTitleChart;

    /**
     * 中方教师性别统计
     */
    private Map<String, Long> ChineseTeacherGenderChart;

    /**
     * 中方教师职位统计
     */
    private Map<String, Long> ChineseTeacherTitleChart;


}
