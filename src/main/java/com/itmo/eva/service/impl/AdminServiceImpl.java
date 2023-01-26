package com.itmo.eva.service.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmo.eva.common.ErrorCode;
import com.itmo.eva.exception.BusinessException;
import com.itmo.eva.model.entity.Admin;
import com.itmo.eva.model.vo.AdminVo;
import com.itmo.eva.service.AdminService;
import com.itmo.eva.mapper.AdminMapper;
import com.itmo.eva.utils.JwtUtil;
import com.itmo.eva.utils.SpecialUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PatchMapping;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * @author chenjiahan
 * @description 针对表【e_admin(管理员表)】的数据库操作Service实现
 * @createDate 2023-01-21 10:48:59
 */
@Service
@Slf4j
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin>
        implements AdminService {

    @Resource
    private AdminMapper adminMapper;


    /**
     * 验证登陆并返回token
     * @param username 账号
     * @param password 密码
     * @return token
     */
    @Override
    public String userLogin(String username, String password) {
        if (StringUtils.isAnyBlank(username, password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码为空");
        }
        if (SpecialUtil.isSpecialChar(username) || SpecialUtil.isSpecialChar(password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "包含特殊字符");
        }
        // 查询用户
        Admin admin = adminMapper.getUserByUsernameAndPassword(username, password);
        // 用户不存在
        if (admin == null) {
            log.info("user login failed, username cannot match password!");
            throw new BusinessException(ErrorCode.USER_NOT_FOUND,"用户不存在或密码");
        }
        // 用户是否拥有权限
        if (admin.getRole() != 1) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 生成token
        HashMap<String, String> jwt = new HashMap<>();
        jwt.put("username", username);
        jwt.put("id", admin.getId().toString());
        String token = JwtUtil.generateToken(jwt);
        return token;
    }

    /**
     * 管理员信息
     *
     * @param token token值
     * @return 当前用户登陆信息
     */
    @Override
    public AdminVo getLoginUser(String token) {
        // 对传回来的token进行解析 -> 解析出token中对应用户的id
        DecodedJWT decodedJWT = JwtUtil.decodeToken(token);
        Integer id = Integer.valueOf(decodedJWT.getClaim("id").asString());

        Admin admin = adminMapper.selectById(id);

        if (admin == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }

        AdminVo adminInfo = new AdminVo();
        BeanUtils.copyProperties(admin, adminInfo);
        adminInfo.setId(admin.getId().longValue());
        return adminInfo;
    }


}




