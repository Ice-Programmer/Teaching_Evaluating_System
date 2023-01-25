package com.itmo.eva.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 一级指标表
 * @TableName e_mark_history
 */
@TableName(value ="e_mark_history")
@Data
public class MarkHistory implements Serializable {
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
     * 课程主键
     */
    private Integer cid;

    /**
     * 评价主键
     */
    private Integer eid;

    /**
     * 分数
     */
    private Integer score;

    /**
     * 评价体系主键
     */
    private Integer sid;

    /**
     * 学生主键
     */
    private Integer aid;

    /**
     * 0默认未完成，1为完成该一级测评
     */
    private Integer state;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}