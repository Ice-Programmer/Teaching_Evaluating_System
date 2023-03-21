package com.itmo.eva.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itmo.eva.common.ErrorCode;
import com.itmo.eva.exception.BusinessException;
import com.itmo.eva.mapper.*;
import com.itmo.eva.model.entity.*;
import com.itmo.eva.model.vo.chart.BasicChartsVo;
import com.itmo.eva.model.vo.chart.DetailChartVo;
import com.itmo.eva.model.vo.chart.ScoreVo;
import com.itmo.eva.model.vo.chart.TeacherRankChartVo;
import com.itmo.eva.model.vo.teacher.TeacherGenderVo;
import com.itmo.eva.model.vo.teacher.TeacherNameVo;
import com.itmo.eva.model.vo.teacher.TeacherTitleVo;
import com.itmo.eva.service.ChartService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChartServiceImpl implements ChartService {

    @Resource
    private TeacherMapper teacherMapper;

    @Resource
    private RedlineHistoryMapper redlineHistoryMapper;

    @Resource
    private AverageScoreMapper averageScoreMapper;

    @Resource
    private ScoreHistoryMapper scoreHistoryMapper;

    @Resource
    private EvaluateMapper evaluateMapper;

    @Resource
    private SystemMapper systemMapper;

    /**
     * 获取教师基本信息
     *
     * @return 教师性别职位信息
     */
    @Override
    public BasicChartsVo getStaticInfo() {
        BasicChartsVo chartsVo = new BasicChartsVo();
        // 1. 俄方教师基本情况
        List<Teacher> russianTeacher = teacherMapper.getRussianTeacher();

        // 俄方教师性别情况
        TeacherGenderVo russianGenderChart = getTeacherGenderNum(russianTeacher);
        Map<String, Long> RussianTeacherGenderMap = new HashMap<>();
        RussianTeacherGenderMap.put("男", russianGenderChart.getMaleNum());
        RussianTeacherGenderMap.put("女", russianGenderChart.getFemaleNum());
        chartsVo.setRussianTeacherGenderChart(RussianTeacherGenderMap);

        // 俄方教师职位情况
        TeacherTitleVo teacherTitleChart = getTeacherTitleNum(russianTeacher);
        Map<String, Long> RussianTeacherTitleMap = new HashMap<>();
        RussianTeacherTitleMap.put("教授", teacherTitleChart.getProfessorNum());
        RussianTeacherTitleMap.put("副教授", teacherTitleChart.getAssociateProfessorNum());
        RussianTeacherTitleMap.put("讲师", teacherTitleChart.getLecturerNum());
        chartsVo.setRussianTeacherTitleChart(RussianTeacherTitleMap);

        //2. 中方教师基本情况
        List<Teacher> chineseTeacher = teacherMapper.getChineseTeacher();

        // 中方教师性别情况
        TeacherGenderVo chineseGenderChart = getTeacherGenderNum(chineseTeacher);
        Map<String, Long> ChineseTeacherGenderMap = new HashMap<>();
        ChineseTeacherGenderMap.put("男", chineseGenderChart.getMaleNum());
        ChineseTeacherGenderMap.put("女", chineseGenderChart.getFemaleNum());
        chartsVo.setChineseTeacherGenderChart(ChineseTeacherGenderMap);

        // 中方教师职位情况
        TeacherTitleVo chineseTitleChart = getTeacherTitleNum(chineseTeacher);
        Map<String, Long> ChineseTeacherTitleMap = new HashMap<>();
        ChineseTeacherTitleMap.put("教授", chineseTitleChart.getProfessorNum());
        ChineseTeacherTitleMap.put("副教授", chineseTitleChart.getAssociateProfessorNum());
        ChineseTeacherTitleMap.put("讲师", chineseTitleChart.getLecturerNum());
        chartsVo.setChineseTeacherTitleChart(ChineseTeacherTitleMap);

        return chartsVo;
    }

    /**
     * 获取教师指标雷达图
     *
     * @param tid 教师id
     * @return 教师一级指标分数
     */
    @Override
    public List<DetailChartVo> getDetailChartList(Long tid) {
        List<DetailChartVo> detailChartVoList = new ArrayList<>();

        LambdaQueryWrapper<AverageScore> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AverageScore::getTid, tid);
        // 获取制定教师信息
        List<AverageScore> teacherScoreList = averageScoreMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(teacherScoreList)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 根据教师一级指标将教师进行分组
        Map<Integer, List<AverageScore>> systemScoreMap = teacherScoreList.stream().collect(Collectors.groupingBy(AverageScore::getSid));
        for (Integer sid : systemScoreMap.keySet()) {
            DetailChartVo chineseDetailChart = new DetailChartVo();

            List<AverageScore> averageScores = systemScoreMap.get(sid);
            // 取出该教师在所有一级指标分数
            List<Integer> totalScore = averageScores.stream().map(AverageScore::getScore).collect(Collectors.toList());
            // 计算出平均分
            OptionalDouble optionalAverage = totalScore.stream().mapToDouble(Integer::doubleValue).average();
            if (optionalAverage != null && optionalAverage.isPresent()) {
                String systemName = systemMapper.getChineseNameById(sid);
                chineseDetailChart.setSid(sid);
                chineseDetailChart.setSystemName(systemName);
                chineseDetailChart.setSystemScore(optionalAverage.getAsDouble());
            }
            detailChartVoList.add(chineseDetailChart);

        }

        return detailChartVoList;
    }

    /**
     * 获取前十名教师信息
     *
     * @param identity 国籍
     * @return
     */
    @Override
    public List<TeacherRankChartVo> getTeacherRankChart(Integer identity) {
        // 获取最近一次评测id
        Integer eid = evaluateMapper.getCurrentEvaluateId();
        LambdaQueryWrapper<ScoreHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ScoreHistory::getIdentity, identity);
        queryWrapper.eq(ScoreHistory::getEid, eid);
        queryWrapper.orderByDesc(ScoreHistory::getScore).last("limit 10");

        // 获取前十名的教师信息
        List<ScoreHistory> teacherScoreList = scoreHistoryMapper.selectList(queryWrapper);
        List<TeacherRankChartVo> teacherRankChartVoList = teacherScoreList.stream().map(teacher -> {
            Integer teacherId = teacher.getTid();
            String teacherName = teacherMapper.getNameById(teacherId.longValue());
            TeacherRankChartVo teacherRankChartVo = new TeacherRankChartVo();
            teacherRankChartVo.setTeacherName(teacherName);
            teacherRankChartVo.setScore(teacher.getScore().doubleValue());

            // 获取该教师一级评级细则分数 => 从average中取分数
            LambdaQueryWrapper<AverageScore> averageLambdaQueryWrapper = new LambdaQueryWrapper<>();
            averageLambdaQueryWrapper.eq(AverageScore::getTid, teacherId);
            averageLambdaQueryWrapper.eq(AverageScore::getEid, eid);
            // 取出数据
            List<AverageScore> teacherSystemScoreList = averageScoreMapper.selectList(averageLambdaQueryWrapper);

            List<ScoreVo> systemScoreList = teacherSystemScoreList.stream().map(score -> {
                ScoreVo systemScore = new ScoreVo();
                Integer sid = score.getSid();
                String systemName = systemMapper.getChineseNameById(sid);
                systemScore.setSystem(systemName);
                systemScore.setScore(score.getScore().doubleValue());
                return systemScore;
            }).collect(Collectors.toList());
            teacherRankChartVo.setSystemScoreList(systemScoreList);

            return teacherRankChartVo;
        }).collect(Collectors.toList());

        return teacherRankChartVoList;
    }

    @Override
    public List<ScoreVo> getTeacherDetailScoreChart(Integer eid, Integer tid) {
        // 判断评测是否在进行中
        Evaluate evaluate = evaluateMapper.selectById(eid);
        if (evaluate == null || evaluate.getStatus() == 1) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "评测不存在或正在进行中");
        }
        LambdaQueryWrapper<AverageScore> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AverageScore::getEid, eid);
        queryWrapper.eq(AverageScore::getTid, tid);
        List<AverageScore> teacherScoreList = averageScoreMapper.selectList(queryWrapper);
        if (teacherScoreList == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        List<ScoreVo> teacherDetailChart = teacherScoreList.stream().map(teacher -> {
            ScoreVo scoreVo = new ScoreVo();
            Integer sid = teacher.getSid();
            String systemName = systemMapper.getChineseNameById(sid);
            scoreVo.setScore(teacher.getScore().doubleValue());
            scoreVo.setSystem(systemName);
            return scoreVo;
        }).collect(Collectors.toList());

        return teacherDetailChart;
    }

    /**
     * 获取红线教师
     * @return
     */
    @Override
    public List<TeacherNameVo> getRedlineTeacher() {
        List<RedlineHistory> redlineHistoryList = redlineHistoryMapper.selectList(null);
        if (CollectionUtils.isEmpty(redlineHistoryList)) {
            return Collections.emptyList();
        }
        List<TeacherNameVo> teacherNameVoList = redlineHistoryList.stream().map(redlineHistory -> {
            TeacherNameVo teacherNameVo = new TeacherNameVo();
            Integer tid = redlineHistory.getTid();
            String teacherName = teacherMapper.getNameById(tid.longValue());
            teacherNameVo.setName(teacherName);
            teacherNameVo.setId(tid.longValue());
            return teacherNameVo;
        }).collect(Collectors.toList());
        List<TeacherNameVo> redlineList = new ArrayList<>();
        for (TeacherNameVo teacherNameVo : teacherNameVoList) {
            if (!redlineList.contains(teacherNameVo)) {
                redlineList.add(teacherNameVo);
            }
        }
        return redlineList;
    }

    /**
     * 获取教师性别统计
     *
     * @param teacherList 教师列表
     * @return 性别统计
     */
    private TeacherGenderVo getTeacherGenderNum(List<Teacher> teacherList) {
        TeacherGenderVo teacherGenderVo = new TeacherGenderVo();
        long maleNum = teacherList.stream().filter(teacher -> teacher.getSex() == 1).count();
        long femaleNum = teacherList.stream().filter(teacher -> teacher.getSex() == 0).count();
        teacherGenderVo.setMaleNum(maleNum);
        teacherGenderVo.setFemaleNum(femaleNum);

        return teacherGenderVo;
    }

    /**
     * 获取教师职位统计
     *
     * @param teacherList 教师列表
     * @return 职位统计
     */
    private TeacherTitleVo getTeacherTitleNum(List<Teacher> teacherList) {
        TeacherTitleVo teacherTitleChart = new TeacherTitleVo();
        long professorNum = teacherList.stream().filter(teacher -> teacher.getTitle() == 1).count();
        long associateProfessorNum = teacherList.stream().filter(teacher -> teacher.getTitle() == 2).count();
        long lecturerNum = teacherList.stream().filter(teacher -> teacher.getTitle() == 3).count();
        teacherTitleChart.setProfessorNum(professorNum);
        teacherTitleChart.setAssociateProfessorNum(associateProfessorNum);
        teacherTitleChart.setLecturerNum(lecturerNum);
        return teacherTitleChart;
    }



}
