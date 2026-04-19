package org.ysling.litemall.core.satoken.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.jwt.SaJwtTemplate;
import cn.dev33.satoken.jwt.SaJwtUtil;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import cn.hutool.jwt.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ysling
 */
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {

    /**
     * Sa-Token 整合 jwt (Simple 简单模式)
     */
    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }

    /**
     * 自定义 SaJwtUtil 生成 token 的算法
     */
    @Autowired
    public void setSaJwtTemplate() {
        SaJwtUtil.setSaJwtTemplate(new SaJwtTemplate() {
            @Override
            public String generateToken(JWT jwt, String key) {
                return super.generateToken(jwt, key);
            }
        });
    }

    /**
     * 注册 Sa-Token 拦截器，打开注解式鉴权功能
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，打开注解式鉴权功能
        List<String>  patterns = new ArrayList<>();
        patterns.add("/**");
        registry.addInterceptor(new SaInterceptor()).addPathPatterns(patterns);
    }


}
