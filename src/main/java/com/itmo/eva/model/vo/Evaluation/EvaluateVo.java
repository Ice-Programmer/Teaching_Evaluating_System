package com.itmo.eva.model.vo.Evaluation;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 评测表
 * @TableName e_evaluate
 */
@TableName(value ="e_evaluate")
@Data
public class EvaluateVo implements Serializable {
    /**
     * id
     */
    private Integer id;

    /**
     * 评测名称
     */
    private String name;

    /**
     * 创建时间
     */
    private String create_time;

    /**
     * 开始时间
     */
    private String start_time;

    /**
     * 结束时间
     */
    private String e_time;

    /**
     * 发布状态（0-评测结束 1-正在评测）
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
}