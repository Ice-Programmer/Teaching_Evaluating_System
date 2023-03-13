package com.itmo.eva.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 权重表
 * @TableName e_weight
 */
@TableName(value ="e_weight")
@Data
public class Weight implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 一级指标的主键
     */
    private Integer lid;

    /**
     * 一级指标对应的权重
     */
    private BigDecimal weight;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}