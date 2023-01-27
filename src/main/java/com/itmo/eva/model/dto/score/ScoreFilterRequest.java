package com.itmo.eva.model.dto.score;

import lombok.Data;

import java.io.Serializable;

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

    private static final long serialVersionUID = 1L;

}
