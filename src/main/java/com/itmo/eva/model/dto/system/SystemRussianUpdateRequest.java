package com.itmo.eva.model.dto.system;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SystemRussianUpdateRequest implements Serializable {

    /**
     * id
     */
    private Integer id;

    /**
     * 一级评价名称
     */
    private String name;

    /**
     * 储存二级评价
     */
    private List<String> children;

    private static final long serialVersionUID = 1L;


}
