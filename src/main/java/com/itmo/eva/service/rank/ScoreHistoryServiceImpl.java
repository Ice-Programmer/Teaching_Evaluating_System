package com.itmo.eva.service.rank;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmo.eva.common.ErrorCode;
import com.itmo.eva.exception.BusinessException;
import com.itmo.eva.mapper.*;
import com.itmo.eva.model.entity.*;
import com.itmo.eva.model.entity.System;
import com.itmo.eva.model.vo.ScoreHistoryVo;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

/**
 * @author chenjiahan
 * @description 针对表【e_score_history(总分表)】的数据库操作Service实现
 * @createDate 2023-01-25 13:28:07
 */
@Service
public class ScoreHistoryServiceImpl extends ServiceImpl<ScoreHistoryMapper, ScoreHistory>
        implements ScoreHistoryService {

    @Resource
    private EvaluateMapper evaluateMapper;

    @Resource
    private AverageScoreMapper averageScoreMapper;

    @Resource
    private MarkHistoryMapper markHistoryMapper;

    @Resource
    private TeacherMapper teacherMapper;

    @Resource
    private SystemMapper systemMapper;


    /**
     * 判断是否存在分数
     */
    @PostConstruct
    public void init() {
        // 1.获取所有评测的id 【已经结束】
        List<Integer> evaluateIds = evaluateMapper.getAllEndEvaluation().stream().map(Evaluate::getId).collect(Collectors.toList());
        for (Integer e : evaluateIds) {
            Integer isExit = averageScoreMapper.getByEid(e);
            // 判断是否为空
            if (isExit == null) {
                // 如果为空，计算该评测的平均分
                calculateAverageScore(e);
            }
        }
    }


    /**
     * 获取中方老师所有的分数
     *
     * @return 中方分数
     */
    @Override
    public List<ScoreHistoryVo> getChineseScore(Integer eid) {

        // 判断当前评测是否正在进行
        Integer status = evaluateMapper.getStatusById(eid);
        if (status == 1) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "评测正在进行中");
        }
        // 获取所有的中国老师
        List<Teacher> chineseTeacher = teacherMapper.getChineseTeacher();
        List<ScoreHistoryVo> historyVoList = new ArrayList<>();
        for (Teacher teacher : chineseTeacher) {
            ScoreHistoryVo scoreHistoryVo = new ScoreHistoryVo();
            // 获取教师id
            Long tid = teacher.getId();
            // 在average表中找到对应的所有一级评价分数
            Map<Integer, Integer> detailScore = averageScoreMapper.getByEidAndTid(eid, tid).stream().collect(Collectors.toMap(AverageScore::getSid, AverageScore::getScore));

            Integer score = detailScore.values().stream().reduce(Integer::sum).orElse(0);
            scoreHistoryVo.setDetailScore(detailScore);
            scoreHistoryVo.setTid(tid.intValue());
            scoreHistoryVo.setTeacher(teacher.getName());
            scoreHistoryVo.setScore(new BigDecimal(score / 10.0));
            scoreHistoryVo.setEid(eid);
            scoreHistoryVo.setIdentity(teacher.getIdentity());
            historyVoList.add(scoreHistoryVo);
        }

        return historyVoList;
    }

    /**
     * 获取俄方老师所有分数
     *
     * @return 俄方分数
     */
    @Override
    public List<ScoreHistoryVo> getRussianScore(Integer eid) {
        // 获取所有的俄方老师
        List<Teacher> russianTeacher = teacherMapper.getRussianTeacher();
        List<ScoreHistoryVo> historyVoList = new ArrayList<>();
        for (Teacher teacher : russianTeacher) {
            ScoreHistoryVo scoreHistoryVo = new ScoreHistoryVo();
            // 获取教师id
            Long tid = teacher.getId();
            // 在average表中找到对应的所有一级评价分数
            Map<Integer, Integer> detailScore = averageScoreMapper.getByEidAndTid(eid, tid).stream().collect(Collectors.toMap(AverageScore::getSid, AverageScore::getScore));

            Integer score = detailScore.values().stream().reduce(Integer::sum).orElse(0);
            scoreHistoryVo.setDetailScore(detailScore);
            scoreHistoryVo.setTid(tid.intValue());
            scoreHistoryVo.setScore(new BigDecimal(score / 10.0));
            scoreHistoryVo.setEid(eid);
            scoreHistoryVo.setTeacher(teacher.getName());
            scoreHistoryVo.setIdentity(teacher.getIdentity());
            historyVoList.add(scoreHistoryVo);
        }

        return historyVoList;
    }

    /**
     * 计算平均分 【总分】
     * @param eid 评测的id
     */
    private void calculateAverageScore(Integer eid) {
        // 获取所有老师信息
        List<Teacher> teacherList = teacherMapper.selectList(null);

        // 遍历所有教师，来计算每一个老师对应所有一级指标的平均分
        for (Teacher teacher : teacherList) {
            Long tid = teacher.getId();     // 教师id
            Integer identity = teacher.getIdentity();   // 国籍
            // 获取该教师的一级评价内容
            List<System> systemList = systemMapper.getCountByKind(identity);
            for (System system : systemList) {
                Integer sid = system.getSid(); // 一级评价id
                LambdaQueryWrapper<MarkHistory> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(MarkHistory::getEid, eid)   // 某一次评测
                        .eq(MarkHistory::getTid, tid)       // 某一位老师
                        .eq(MarkHistory::getSid, sid);      // 某一个一级指标
                // 这位教师在这一个一级评论下的所有分数list
                List<Integer> scoreList = markHistoryMapper.selectList(queryWrapper).stream().map(MarkHistory::getScore).collect(Collectors.toList());
                // 该教师这一项一级指标的平均值
                OptionalDouble optionalAverage = scoreList.stream().mapToDouble(Integer::doubleValue).average();
                if (optionalAverage != null && optionalAverage.isPresent()) {
                    double average = optionalAverage.getAsDouble();
                    AverageScore averageScore = new AverageScore();
                    averageScore.setTid(tid);
                    averageScore.setScore((int) average);
                    averageScore.setSid(sid);
                    averageScore.setEid(eid);
                    averageScoreMapper.insert(averageScore);
                }

            }

        }

    }
}




