package com.itmo.eva.config;

import com.itmo.eva.intercepts.PermissionIntercept;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 设置拦截器
 */
@Configuration
public class PermissionWebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PermissionIntercept())
                .addPathPatterns("/**")    // 拦截哪些页面
                .excludePathPatterns("/admin/login", "/admin/logout");   // 放行哪些页面
    }
}