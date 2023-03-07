package com.itmo.eva.model.dto.system;

import lombok.Data;

import java.io.Serializable;

@Data
public class SystemAddRequest implements Serializable {

    /**
     * 一级评测id
     */
    private Integer sid;

    /**
     * 评测名称
     */
    private String name;

    /**
     * 评测英文名
     */
    private String eName;

    /**
     * 国籍 0-俄罗斯 1-中国
     */
    private Integer kind;


    private static final long serialVersionUID = 1L;

}
