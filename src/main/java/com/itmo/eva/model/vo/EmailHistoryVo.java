package com.itmo.eva.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 邮件记录表
 * @TableName e_email_history
 */
@TableName(value ="e_email_history")
@Data
public class EmailHistoryVo implements Serializable {
    /**
     * id
     */
    private Integer id;

    /**
     * 操作人姓名
     */
    private String name;

    /**
     * 操作
     */
    private String operation;

    /**
     * 提交时间
     */
    private String operationTime;

    /**
     * 发送状态
     */
    private String state;

    private static final long serialVersionUID = 1L;
}