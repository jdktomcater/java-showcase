package com.jdktomcat.showcase.system.design.order.service.web.config;

import com.jdktomcat.showcase.system.design.order.service.web.interceptor.risk.RiskCheckInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final RiskCheckInterceptor riskCheckInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(riskCheckInterceptor).addPathPatterns("/**");
    }
}
