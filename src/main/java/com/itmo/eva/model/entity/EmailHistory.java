package com.itmo.eva.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 邮件记录表
 * @TableName e_email_history
 */
@TableName(value ="e_email_history")
@Data
public class EmailHistory implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
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
    private String submit_time;

    /**
     * 发送状态
     */
    private Integer state;

    /**
     * 收件人
     */
    private String recipient;

    /**
     * 操作时间
     */
    private String operationTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}