package com.itmo.eva.model.dto.system;

import lombok.Data;

import java.io.Serializable;

/**
 * 获取二级评价分数
 */
@Data
public class SecondSystemQueryRequest implements Serializable {

    /**
     * 二级指标id
     */
    private Integer secondId;

    /**
     * 教师id
     */
    private Integer teacherId;

    /**
     * 数据表名称
     */
    private String tableName;

    private static final long serialVersionUID = 1L;
}
