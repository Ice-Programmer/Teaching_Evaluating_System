package com.itmo.eva.controller;


import com.itmo.eva.common.BaseResponse;
import com.itmo.eva.common.ErrorCode;
import com.itmo.eva.common.ResultUtils;
import com.itmo.eva.exception.BusinessException;
import com.itmo.eva.model.dto.admin.AdminLoginRequest;
import com.itmo.eva.model.vo.AdminVo;
import com.itmo.eva.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.ip2region.core.Ip2regionSearcher;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 管理员接口
 */
@RestController
@Slf4j
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private AdminService adminService;

    /**
     * 用户登陆
     *
     * @param adminLoginRequest 管理员登陆请求体
     * @return token
     */
    @PostMapping("/login")
    public BaseResponse<String> userLogin(@RequestBody AdminLoginRequest adminLoginRequest, HttpServletRequest request) {
        if (adminLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号密码不为空");
        }
        String username = adminLoginRequest.getUsername();
        String password = adminLoginRequest.getPassword();
        if (StringUtils.isAnyBlank(username, password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String token = adminService.userLogin(username, password, request);
        return ResultUtils.success(token);
    }

    /**
     * 获取登陆信息
     *
     * @param token token
     * @return 登陆信息
     */
    @GetMapping("/get/login")
    public BaseResponse<AdminVo> getLoginUser(@RequestHeader("token") String token) {
        AdminVo adminInfo = adminService.getLoginUser(token);
        if (adminInfo == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户信息不存在");
        }
        return ResultUtils.success(adminInfo);
    }

}
