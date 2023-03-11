package com.itmo.eva.model.vo.system;

import lombok.Data;

import java.io.Serializable;

@Data
public class FirstSystemScoreVo implements Serializable {

    /**
     * 一级指标名称
     */
    private String name;

    /**
     * 一级指标id
     */
    private Integer sid;

    /**
     * 一级指标平均分数
     */
    private Double score;

    private static final long serialVersionUID = 1L;

}
