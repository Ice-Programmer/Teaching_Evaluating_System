package com.itmo.eva.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 总分表
 * @TableName e_score_history
 */
@Data
public class ScoreHistoryVo implements Serializable {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 教师主键
     */
    private Integer tid;


    /**
     * 总分
     */
    private BigDecimal score;

    /**
     * 评价主键
     */
    private Integer eid;

    /**
     * 教师名称
     */
    private String teacher;

    /**
     * 详细的一级指标分数
     */
    private Map<Integer, Integer> detailScore;

    /**
     * 国籍
     */
    private Integer identity;

    private static final long serialVersionUID = 1L;
}