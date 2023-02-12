package com.itmo.eva.model.vo.classes;

import lombok.Data;

import java.io.Serializable;

@Data
public class AutomationClass implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String cid;
}
