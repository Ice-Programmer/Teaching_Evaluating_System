package com.itmo.eva.intercepts;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.itmo.eva.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class PermissionIntercept implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        try {
            // 对Token进行解析
            DecodedJWT dj = JwtUtil.decodeToken(token);
            // 获取数据
            String username = dj.getClaim("username").asString();
            String id = dj.getClaim("id").asString();

            // 计算当前令牌是否超过授权时间的一般，超过后自动续期
            Long expTime = dj.getExpiresAt().getTime();
            Long iatTime = dj.getIssuedAt().getTime();
            Long nowTime = new Date().getTime();
            if ((nowTime - iatTime) > (expTime - iatTime) / 2) {
                // 生成新的jwt
                log.info("令牌续约");
                Map<String, String> payload = new HashMap<>();
                payload.put("username", username); // 加入一些非敏感的用户信息
                payload.put("id", id);    // 加入一些非敏感的用户信息
                String newJwt = JwtUtil.generateToken(payload);
                // 加入返回头
                response.addHeader("access-token", newJwt);
            }
        } catch (JWTDecodeException e) {
            log.error("令牌错误");
            return false;

        } catch (TokenExpiredException e) {
            log.error("令牌过期");
            return false;
        }
        return true;
    }
}
