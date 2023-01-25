package com.itmo.eva.service.charts;

import com.itmo.eva.mapper.*;
import com.itmo.eva.model.entity.*;
import com.itmo.eva.model.entity.System;
import com.itmo.eva.model.vo.chart.ChartsVo;
import com.itmo.eva.model.vo.chart.ScoreVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChartServiceImpl implements ChartService{

    @Resource
    private TeacherMapper teacherMapper;

    @Resource
    private TitleMapper titleMapper;

    @Resource
    private RedlineHistoryMapper redlineHistoryMapper;

    @Resource
    private AverageScoreMapper averageScoreMapper;

    @Resource
    private SystemMapper systemMapper;

//    private Map<String, Objects> map;
//
//    @PostConstruct
//    public void init() {
//
//    }

    /**
     * 获取俄方人员信息
     * @return
     */
    @Override
    public ChartsVo getStaticInfo() {

        /**************************俄方人数信息**********************************/
        ChartsVo chartsInfo = new ChartsVo();
        List<Teacher> russianTeacher = teacherMapper.getRussianTeacher();
        // 获取性别信息
        long maleCount = russianTeacher.stream().filter(teacher -> teacher.getSex() == 1).count();
        long femaleCount = russianTeacher.stream().filter(teacher -> teacher.getSex() == 0).count();
        List<Title> titleList = titleMapper.selectList(null);

        Map<String, Long> russianMap = new HashMap<>();
        // 获取职称信息
        for (Title title : titleList) {
            long count = russianTeacher.stream().filter(teacher -> Objects.equals(teacher.getTitle(), title.getId())).count();
            russianMap.put(title.getName(), count);
        }
        russianMap.put("男", maleCount);
        russianMap.put("女", femaleCount);
        // 俄方基本情况
        chartsInfo.setRussianPeopleChart(russianMap);
        /**************************中方人数信息**********************************/
        List<Teacher> chineseTeacher = teacherMapper.getChineseTeacher();
        maleCount = chineseTeacher.stream().filter(teacher -> teacher.getSex() == 1).count();
        femaleCount = chineseTeacher.stream().filter(teacher -> teacher.getSex() == 0).count();

        Map<String, Long> chineseMap = new HashMap<>();
        // 获取职称信息
        for (Title title : titleList) {
            long count = chineseTeacher.stream().filter(teacher -> Objects.equals(teacher.getTitle(), title.getId())).count();
            chineseMap.put(title.getName(), count);
        }
        chineseMap.put("男", maleCount);
        chineseMap.put("女", femaleCount);
        // 中方基本情况
        chartsInfo.setChinesePeopleChart(chineseMap);
        /**************************红线信息**********************************/
        Map<Long, String> teacherMap = teacherMapper.selectList(null).stream()
                .collect(Collectors.toMap(Teacher::getId, Teacher::getName));

        List<RedlineHistory> redlineHistoryList = redlineHistoryMapper.selectList(null);
        List<String> redLineTeacherName = redlineHistoryList.stream().map(redlineHistory -> {
            String name = teacherMap.get(redlineHistory.getTid().longValue());
            return name;
        }).collect(Collectors.toList());
        chartsInfo.setRedLine(redLineTeacherName);
        /**************************中方教师指标**********************************/
        // 获取所有一级评价
        Map<Integer, String> systemMap = systemMapper.selectList(null).stream().collect(Collectors.toMap(System::getId, System::getName));
        Map<String, List<ScoreVo>> chineseScoreMap = new HashMap<>();
        for (Teacher teacher : chineseTeacher) {
            // 获取该老师所有的评测信息
            List<AverageScore> scoreList = averageScoreMapper.getScoreByTid(teacher.getId());
            List<ScoreVo> scoreVoList = new ArrayList<>();
            for (AverageScore score : scoreList) {
                ScoreVo scoreVo = new ScoreVo();
                scoreVo.setSystem(systemMap.get(score.getSid()));
                scoreVo.setScore(score.getScore() / 10.0);
                scoreVoList.add(scoreVo);
            }
            chineseScoreMap.put(teacher.getName(), scoreVoList);
        }
        chartsInfo.setChinaScore(chineseScoreMap);
        /**************************俄方教师指标**********************************/
        Map<String, List<ScoreVo>> russianScoreMap = new HashMap<>();
        for (Teacher teacher : russianTeacher) {
            // 获取该老师所有的评测信息
            List<AverageScore> scoreList = averageScoreMapper.getScoreByTid(teacher.getId());
            List<ScoreVo> scoreVoList = new ArrayList<>();
            for (AverageScore score : scoreList) {
                ScoreVo scoreVo = new ScoreVo();
                scoreVo.setSystem(systemMap.get(score.getSid()));
                scoreVo.setScore(score.getScore() / 10.0);
                scoreVoList.add(scoreVo);
            }
            russianScoreMap.put(teacher.getName(), scoreVoList);
        }
        chartsInfo.setRussianScore(russianScoreMap);
        return chartsInfo;
    }


}
