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
                .addPathPatterns("/api/link/*/**","/api/group/*/**","/api/domain/*/**")
                //排除不拦截的
                .excludePathPatterns("/api/domain/v1/test","/api/link/*/check");

    }

}
