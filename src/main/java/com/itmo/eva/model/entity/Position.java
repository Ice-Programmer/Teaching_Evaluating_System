package com.itmo.eva.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 职位表
 * @TableName e_position
 */
@TableName(value ="e_position")
@Data
public class Position implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 职位名称
     */
    private String name;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}