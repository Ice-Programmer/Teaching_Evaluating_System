package com.itmo.eva.service;

import com.itmo.eva.model.entity.Admin;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itmo.eva.model.vo.AdminVo;

import javax.servlet.http.HttpServletRequest;

/**
* @author chenjiahan
* @description 针对表【e_admin(管理员表)】的数据库操作Service
* @createDate 2023-01-21 10:48:59
*/
public interface AdminService extends IService<Admin> {


    /**
     * 用户登陆
     *
     * @param username 账号
     * @param password 密码
     * @return token
     */
    String userLogin(String username, String password, HttpServletRequest request);

    /**
     * 用户信息
     * @param token token值
     * @return 当前登陆用户信息
     */
    AdminVo getLoginUser(String token);



}
