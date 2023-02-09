package com.itmo.eva.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ClassVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Integer id;

    private String cid;
}
