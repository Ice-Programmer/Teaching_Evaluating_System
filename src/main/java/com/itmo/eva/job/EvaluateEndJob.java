package com.itmo.eva.job;


import com.itmo.eva.mapper.EvaluateMapper;
import com.itmo.eva.model.entity.Evaluate;
import com.itmo.eva.service.RedlineHistoryService;
import com.itmo.eva.service.ScoreHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 用来判断评测是否结束
 */
@Component
@Slf4j
public class EvaluateEndJob {

    @Resource
    private EvaluateMapper evaluateMapper;

    @Resource
    private ScoreHistoryService scoreHistoryService;

    @Resource
    private RedlineHistoryService redlineHistoryService;

    /**
     * 判断评测过期的操作：
     *  1. 判断是否有正在进行中的评测
     *  2. 判断评测时间是否过期
     *  3. 评测结束后对分数进行计算统合
     */
    // 每天凌晨3点进行判断
    @Scheduled(cron = "0 3 0 * * *")
    @Transactional(rollbackFor = Exception.class)
    public void checkEvaluate() {
        log.info("校验评测中...");
        // 获取仍在进行中的评测
        Evaluate evaluateGoing = evaluateMapper.getEvaluateByStatus();
        if (ObjectUtils.isEmpty(evaluateGoing)) {
            return;
        }
        String endTime = evaluateGoing.getE_time();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date evaluateEndTime = dateFormat.parse(endTime);
            Date nowTime = Calendar.getInstance().getTime();
            // 判断当前日期是否超出规范日期
            if (nowTime.after(evaluateEndTime)) {
                // 超出日期将评测的状态关闭
                evaluateGoing.setStatus(0);
                evaluateMapper.updateById(evaluateGoing);
                // 计算该评测下所有教师的平均分
                scoreHistoryService.calculateScoreAverage(evaluateGoing.getId());
                log.info("{} 评测平均分计算完成", evaluateGoing.getName());
                // 统计红线指标
                redlineHistoryService.recordRedline(evaluateGoing.getId());
                log.info("{} 评测已经结束，当前时间：{}", evaluateGoing.getName(), dateFormat.format(nowTime));
            }
        } catch (ParseException e) {
            log.info("转换评测结束时间出错", e);
            throw new RuntimeException(e);
        }
    }
}