package com.itmo.eva.service;

import com.itmo.eva.model.vo.chart.BasicChartsVo;
import com.itmo.eva.model.vo.chart.DetailChartVo;
import com.itmo.eva.model.vo.chart.ScoreVo;
import com.itmo.eva.model.vo.chart.TeacherRankChartVo;
import com.itmo.eva.model.vo.teacher.TeacherNameVo;

import java.util.List;

public interface ChartService {

    /**
     * 获取教师基本信息
     *
     * @return 教师性别职位信息
     */
    BasicChartsVo getStaticInfo();

    /**
     * 获取中方教师指标雷达图
     *
     * @param tid 教师id
     * @return 教师一级指标分数
     */
    List<DetailChartVo> getDetailChartList(Long tid);

    /**
     * 获取前十名教师信息
     *
     * @param identity 国籍
     * @return
     */
    List<TeacherRankChartVo> getTeacherRankChart(Integer identity);

    /**
     * 获取教师一级指标细则
     *
     * @return
     */
    List<ScoreVo> getTeacherDetailScoreChart(Integer eid, Integer tid);

    /**
     * 获取红线教师信息
     * @return
     */
    List<TeacherNameVo> getRedlineTeacher();
}
