package com.itmo.eva.model.vo.Evaluation;

import lombok.Data;

import java.io.Serializable;

@Data
public class EvaluateNameVo implements Serializable {

    /**
     * 评测id
     */
    private Integer id;

    /**
     * 评测名称
     */
    private String name;

    private static final long serialVersionUID = 1L;

}
