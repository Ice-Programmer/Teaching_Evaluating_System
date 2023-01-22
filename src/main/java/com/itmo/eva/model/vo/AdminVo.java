package com.itmo.eva.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 管理员视图
 */
@Data
public class AdminVo implements Serializable {

    /**
     * 用户名
     */
    private Long id;

    /**
     * 账号
     */
    private String username;

    private static final long serialVersionUID = 1L;
}
