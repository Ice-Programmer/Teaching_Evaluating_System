package com.itmo.eva.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmo.eva.mapper.AverageScoreMapper;
import com.itmo.eva.mapper.RedlineMapper;
import com.itmo.eva.model.entity.AverageScore;
import com.itmo.eva.model.entity.Redline;
import com.itmo.eva.model.entity.RedlineHistory;
import com.itmo.eva.service.RedlineHistoryService;
import com.itmo.eva.mapper.RedlineHistoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author chenjiahan
 * @description 针对表【e_redline_history(红线表)】的数据库操作Service实现
 * @createDate 2023-02-06 19:41:33
 */
@Service
@Slf4j
public class RedlineHistoryServiceImpl extends ServiceImpl<RedlineHistoryMapper, RedlineHistory>
        implements RedlineHistoryService {

    @Resource
    private AverageScoreMapper averageScoreMapper;

    @Resource
    private RedlineMapper redlineMapper;

    @Resource
    private RedlineHistoryMapper redlineHistoryMapper;

    /**
     * 统计教师红线记录
     *
     * @param eid 评测id
     */
    @Override
    public void recordRedline(Integer eid) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        // 找出评测下所有的教师信息
        LambdaQueryWrapper<AverageScore> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AverageScore::getEid, eid);
        List<AverageScore> teacherScoreList = averageScoreMapper.selectList(queryWrapper);
        // 找出所有一级评价的红线的阈值  =>  转为map
        List<Redline> redlineList = redlineMapper.selectList(null);
        Map<Integer, BigDecimal> redlineMap = redlineList.stream().collect(Collectors.toMap(Redline::getSid, Redline::getScore));
        // 判断教师是否低于红线阈值
        // 将所有的评测信息按照一级评测id进行分组判断
        Map<Integer, List<AverageScore>> scoreMap = teacherScoreList.stream().collect(Collectors.groupingBy(AverageScore::getSid));
        scoreMap.forEach((sid, averageScoreList) -> {
            BigDecimal redlineScore = redlineMap.get(sid);
            for (AverageScore averageScore : averageScoreList) {
                double score = averageScore.getScore() / 10.0;
                if (score < redlineScore.doubleValue()) {
                    // 该教师的分数低于红线
                    RedlineHistory redlineHistory = new RedlineHistory();
                    redlineHistory.setTid(averageScore.getTid().intValue());
                    redlineHistory.setScore(new BigDecimal(score));
                    Date time = Calendar.getInstance().getTime();
                    String recordTime = dateFormat.format(time);
                    redlineHistory.setHappen_time(recordTime);

                    redlineHistoryMapper.insert(redlineHistory);
                }
            }
        });
    }
}




