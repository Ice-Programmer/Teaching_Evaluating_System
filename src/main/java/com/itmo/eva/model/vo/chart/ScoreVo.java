package com.itmo.eva.model.vo.chart;

import lombok.Data;

import java.io.Serializable;

@Data
public class ScoreVo implements Serializable {


    /**
     * 一级评测名称
     */
    private String system;

    /**
     * 分数
     */
    private Double score;

}
