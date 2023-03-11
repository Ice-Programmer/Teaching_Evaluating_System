package com.itmo.eva.model.vo.system;

import lombok.Data;

import java.io.Serializable;

/**
 * 二级指标展示
 */
@Data
public class SecondSystemVo implements Serializable {

    /**
     * 二级指标id
     */
    private Integer sid;

    /**
     * 二级指标中文名称
     */
    private String name;

    /**
     * 二级指标英文名称
     */
    private String eName;


    private static final long serialVersionUID = 1L;

}
