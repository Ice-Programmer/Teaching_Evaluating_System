package com.itmo.eva.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 管理员表
 * @TableName e_admin
 */
@TableName(value ="e_admin")
@Data
public class Admin implements Serializable {
    /**
     * 用户名
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 权限
     */
    private Integer role;

    /**
     * 最近一次登录的ip地址
     */
    private String addressIp;

    /**
     * 形式:中国-省份-城市
     */
    private String addressName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}