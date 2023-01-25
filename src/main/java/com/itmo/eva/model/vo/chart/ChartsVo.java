package com.itmo.eva.model.vo.chart;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ChartsVo {

    /**
     * 俄方老师人数总结
     */
    private Map<String, Long> RussianPeopleChart;

    /**
     * 中方老师人数总结
     */
    private Map<String, Long> ChinesePeopleChart;

    /**
     * 俄方老师分数情况
     */
    private Map<String, List<ScoreVo>> RussianScore;

    /**
     * 中方老师分数情况
     */
    private Map<String, List<ScoreVo>> ChinaScore;

    /**
     * 红线名单
     */
    private List<String> redLine;
}
