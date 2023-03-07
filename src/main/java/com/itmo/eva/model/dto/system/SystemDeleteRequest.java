package com.itmo.eva.model.dto.system;

import lombok.Data;

import java.io.Serializable;

@Data
public class SystemDeleteRequest implements Serializable {

    /**
     * 删除评测id
     */
    private Integer id;

    private static final long serialVersionUID = 1L;

}
