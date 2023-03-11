package com.itmo.eva.model.dto.score;

import lombok.Data;

import java.io.Serializable;

/**
 * 教师排名获取
 */
@Data
public class ScoreFilterRequest implements Serializable {

    /**
     * 评测id
     */
    private Integer eid;

    /**
     * 一级评价id
     */
    private Integer sid;

    /**
     * 二级评价id
     */
    private Integer secondId;

    /**
     * 国籍 0-俄罗斯 1-中国
     */
    private Integer identity;


    private static final long serialVersionUID = 1L;

}
