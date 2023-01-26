package com.itmo.eva.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 平均分表
 * @TableName e_average_score
 */
@TableName(value ="e_average_score")
@Data
public class AverageScore implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 教师id
     */
    private Long tid;

    /**
     * 评价体系id
     */
    private Integer sid;

    /**
     * 分数
     */
    private Integer score;

    /**
     * 评价主键
     */
    private Integer eid;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}