package com.itmo.eva.service.impl;
import java.math.BigDecimal;
import com.google.common.collect.Maps;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmo.eva.mapper.MarkHistoryMapper;
import com.itmo.eva.mapper.SystemMapper;
import com.itmo.eva.mapper.TeacherMapper;
import com.itmo.eva.model.entity.MarkHistory;
import com.itmo.eva.model.entity.ScoreHistory;
import com.itmo.eva.model.entity.System;
import com.itmo.eva.model.entity.Teacher;
import com.itmo.eva.model.vo.ScoreHistoryVo;
import com.itmo.eva.service.ScoreHistoryService;
import com.itmo.eva.mapper.ScoreHistoryMapper;
import com.itmo.eva.service.SystemService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private TeacherMapper teacherMapper;

    @Resource
    private MarkHistoryMapper markHistoryMapper;

    @Resource
    private SystemMapper systemMapper;

    /**
     * 获取中方老师所有的分数
     * @return 中方分数
     */
    @Override
    public List<ScoreHistoryVo> getChineseScore() {
        List<ScoreHistory> scoreList = this.list();
        Map<Integer, ScoreHistory> scoreMap = new HashMap<>();
        for (ScoreHistory history : scoreList) {
            scoreMap.put(history.getId(), history);
        }
        // 中方老师所有的一级指标id
        List<Integer> firstId = systemMapper.getChineseFirstSystem().stream().map(System::getId).collect(Collectors.toList());

        // 获取所有中方老师对应的id
        List<Long> teacherId = teacherMapper.getChineseTeacher()
                .stream().map(Teacher::getId).collect(Collectors.toList());
        // 根据教师的id来查询相关的评测结果
/*        for (Long id : teacherId) {
            List<ScoreHistoryVo> scoreVoList = scoreList.stream()
                    .filter(scoreHistory -> scoreHistory.getTid() == id.intValue())
                    .map(scoreHistory -> {
                        // 进行赋值
                        ScoreHistoryVo scoreHistoryVo = new ScoreHistoryVo();
                        BeanUtils.copyProperties(scoreHistoryVo, scoreHistory);
                        scoreHistoryVo.setIdentity(1);
                        // 取mark表里拿一级指标的具体分数【平均】
                        // 全部一级指标 【混在一起】
                        List<MarkHistory> markList = markHistoryMapper.getScoreByTid(id.intValue());
                        // 将不同的一级指标分开
                        for (Integer sid : firstId) {
                            // 对应sid下的所有分数
                            List<Integer> score = markList.stream().filter(markHistory -> markHistory.getSid().equals(sid))
                                    .map(MarkHistory::getScore).collect(Collectors.toList());
                            // 计算平均分
                            BigDecimal averageScore = null;
                            Integer sum = score.stream().reduce(Integer::sum).orElse(0);
                            double e = sum / score.size();


                        }

                        BigDecimal totalScore = null;
                        scoreHistoryVo.setDetailScore(Maps.newHashMap());



                    }).collect(Collectors.toList());
        }*/
        return null;
    }
}




