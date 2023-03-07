package com.itmo.eva.model.dto.system;

import lombok.Data;

import java.io.Serializable;

@Data
public class SystemUpdateRequest implements Serializable {

    /**
     * 评测id
     */
    private Integer id;

    /**
     * 中文名称
     */
    private String cName;

    /**
     * 英文名称
     */
    private String eName;


    private static final long serialVersionUID = 1L;

}
