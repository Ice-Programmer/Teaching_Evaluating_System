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
import net.dreamlu.mica.ip2region.core.Ip2regionSearcher;
import net.dreamlu.mica.ip2region.core.IpInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PatchMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static net.dreamlu.mica.core.utils.StringPool.UNKNOWN;

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

    @Resource
    private Ip2regionSearcher ip2regionSearcher;

    /**
     * 验证登陆并返回token
     *
     * @param username 账号
     * @param password 密码
     * @return token
     */
    @Override
    public String userLogin(String username, String password, HttpServletRequest request) {
        if (StringUtils.isAnyBlank(username, password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码为空");
        }
        if (SpecialUtil.isSpecialChar(username) || SpecialUtil.isSpecialChar(password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "包含特殊字符");
        }
        password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        // 查询用户
        Admin admin = adminMapper.getUserByUsernameAndPassword(username, password);
        // 用户不存在
        if (admin == null) {
            log.info("user login failed, username cannot match password!");
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在或密码错误");
        }

        // 用户是否拥有权限
        if (admin.getRole() != 1) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 生成token
        HashMap<String, String> jwt = new HashMap<>();
        jwt.put("username", username);
        jwt.put("id", admin.getId().toString());

        // 记录登陆用户ip和ip地址，并返回提交结果
        Boolean update = getIpAddr(request, admin.getId());

        if (!update) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "登陆失败，记录ip失败！");
        }

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


    /**
     * 获取用户登陆ip
     *
     * @param request 请求
     * @return 用户登陆地址和ip
     */
    public Boolean getIpAddr(HttpServletRequest request, Integer id) {
        // 获取ip地址
        String ip = this.getIp(request);

        // 根据ip地址获取到address
        IpInfo ipInfo = ip2regionSearcher.memorySearch(ip);
        String address = UNKNOWN;
        assert ipInfo != null;
        if(ipInfo != null){
            address = ipInfo.getAddress();
        }

        // 获取当前用户登陆的信息
        Admin adminInfo = this.getById(id);

        // 设置ip地址
        adminInfo.setAddressIp(ip);
        adminInfo.setAddressName(address);

        boolean update = this.updateById(adminInfo);

        return update;
    }

    /**
     * 获取用户登陆ip
     *
     * @param request 请求
     * @return 用户登陆地址和ip
     */
    @Override
    public String getIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 本机访问
        if ("localhost".equalsIgnoreCase(ip) || "127.0.0.1".equalsIgnoreCase(ip) || "0:0:0:0:0:0:0:1".equalsIgnoreCase(ip)) {
            // 根据网卡取本机配置的IP
            InetAddress inet;
            try {
                inet = InetAddress.getLocalHost();
                ip = inet.getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (null != ip && ip.length() > 15) {
            if (ip.indexOf(",") > 15) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }

        return ip;
    }

}



