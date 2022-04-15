package com.yulin.config;

import com.yulin.interceptor.LoginInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/20
 * @Description:
 */
@Configuration
@Slf4j
public class InterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new LoginInterceptor())
                //添加拦截的路径
                .addPathPatterns("/api/account/*/**","/api/traffic/*/**")
                //排除不拦截的
                .excludePathPatterns("/api/account/*/register","/api/account/*/upload"
                ,"/api/account/*/login","/api/notify/v1/captcha","/api/notify/*/send_code","/api/traffic/*/reduce");

    }

}
