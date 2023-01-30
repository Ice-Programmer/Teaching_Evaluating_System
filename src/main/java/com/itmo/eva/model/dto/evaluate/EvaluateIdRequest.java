package com.itmo.eva.model.dto.evaluate;

import lombok.Data;

import java.io.Serializable;

@Data
public class EvaluateIdRequest implements Serializable {

    /**
     * 评测id
     */
    private Integer eid;

    private static final long serialVersionUID = 1L;
}
