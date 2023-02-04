package com.itmo.eva.model.dto.evaluate;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 评测表
 * @TableName e_evaluate
 */
@Data
public class EvaluateUpdateRequest implements Serializable {
    /**
     * id
     */
    private Integer id;

    /**
     * 评测名称
     */
    private String name;

    /**
     * 开始时间
     */
    private String start_time;

    /**
     * 结束时间
     */
    private String e_time;

    private static final long serialVersionUID = 1L;
}