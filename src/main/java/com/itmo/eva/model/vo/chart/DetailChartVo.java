package com.itmo.eva.model.vo.chart;

import lombok.Data;

import java.io.Serializable;

/**
 * 中方教师指标雷达图
 */
@Data
public class DetailChartVo implements Serializable {
    /**
     * 一级指标id
     */
    private Integer sid;

    /**
     * 一级指标名称
     */
    private String systemName;

    /**
     * 一级指标分数
     */
    private Double systemScore;

    private static final long serialVersionUID = 1L;

}
