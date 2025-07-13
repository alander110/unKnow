package com.project.user.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
     //注册拦截器
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，仅对需要的路径生效
        registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
                .addPathPatterns("/**")
                .excludePathPatterns("/user/login")
                .excludePathPatterns("/user/register")
                .excludePathPatterns("/user/isLogin")
                .excludePathPatterns("/swagger-ui.html")
                .excludePathPatterns("/swagger-ui/**")
                .excludePathPatterns("/v3/api-docs/**")
                .excludePathPatterns("/swagger-resources/**")
                .excludePathPatterns("/webjars/**");
    }

}

