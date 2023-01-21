package com.itmo.eva.model.dto.admin;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理员登陆请求体
 */
@Data
public class AdminLoginRequest implements Serializable {

    private String username;

    private String password;
}
