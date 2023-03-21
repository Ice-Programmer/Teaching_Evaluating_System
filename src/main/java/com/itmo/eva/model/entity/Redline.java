package com.itmo.eva.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 红线指标表
 * @TableName e_redline
 */
@TableName(value ="e_redline")
@Data
public class Redline implements Serializable {
    /**
     * 一级指标id
     */
    private Integer sid;

    /**
     * 红线指标
     */
    private BigDecimal score;




    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}