package com.jdktomcat.demo.limiter.redis.fixed.window.config;

import com.jdktomcat.demo.limiter.redis.fixed.window.interceptor.LimiterInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 通用配置
 *
 * @author timmy
 */
@Configuration
public class ResourcesConfig implements WebMvcConfigurer {

    @Autowired
    private LimiterInterceptor limiterInterceptor;

    /**
     * 自定义拦截规则
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(limiterInterceptor).addPathPatterns("/**");
    }
}