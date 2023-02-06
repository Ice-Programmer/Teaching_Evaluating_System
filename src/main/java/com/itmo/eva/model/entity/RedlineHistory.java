package com.itmo.eva.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 红线表
 * @TableName e_redline_history
 */
@TableName(value ="e_redline_history")
@Data
public class RedlineHistory implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 教师主键
     */
    private Integer tid;

    /**
     * 分数
     */
    private BigDecimal score;

    /**
     * 发生时间
     */
    private String happen_time;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}