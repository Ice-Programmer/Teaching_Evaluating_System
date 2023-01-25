package com.itmo.eva.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * 生成解析token
 *
 * @author chenjiahan
 */
@Slf4j
public class JwtUtil {

    private static final long TIME = 60 * 60 * 1000;     //设置过期时间
    private static final String SIGNATURE = "EduEvaluationSystem";

    /**
     * 生成token
     * @param payload token携带的信息
     * @return token字符串
     */
    public static String generateToken(Map<String,String> payload){
        log.info("生成令牌中...");
        //1.头部默认
        // 指定token过期时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 20);  // 20min
        JWTCreator.Builder builder = JWT.create();
        // 2.构建payload
        payload.forEach(builder::withClaim);
        //3.构建签证
        // 指定签发时间、过期时间 和 签名算法，并返回token
        String token = builder.withIssuedAt(new Date()).withExpiresAt(calendar.getTime()).sign(Algorithm.HMAC256(SIGNATURE));
        return token;
    }

    /**
     * 解析token
     * @param token token字符串
     * @return 解析后的token类
     */
    public static DecodedJWT decodeToken(String token){
        log.info("解析令牌中...");
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SIGNATURE)).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);

        return decodedJWT;
    }

}
