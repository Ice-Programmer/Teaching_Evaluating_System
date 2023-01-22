package com.itmo.eva.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 职称表
 * @TableName e_title
 */
@TableName(value ="e_title")
@Data
public class Title implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 职称名称
     */
    private String name;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}