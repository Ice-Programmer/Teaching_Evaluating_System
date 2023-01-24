package com.itmo.eva.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 评价体系表
 * @TableName e_system
 */
@TableName(value ="e_system")
@Data
public class System implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 指标名称
     */
    private String name;

    /**
     * 评价级别
     */
    private Integer level;

    /**
     * 0为俄方，1为中方
     */
    private Integer kind;

    /**
     * 二级指标指向一级指标
     */
    private Integer sid;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}