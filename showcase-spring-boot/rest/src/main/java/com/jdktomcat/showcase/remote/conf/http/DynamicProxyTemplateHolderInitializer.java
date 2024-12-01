package com.jdktomcat.showcase.remote.conf.http;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * {@link DynamicProxyTemplateHolder}作为静态工具类使用，且注入时机与{@link org.springframework.context.annotation.Bean}不同，
 * 启动报错，因此使用{@link DynamicProxyTemplateHolderInitializer}作为前者的初始化驱动类
 *
 * @author luxun
 */
@Configuration
public class DynamicProxyTemplateHolderInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final ThreadPoolTaskExecutor asyncExecutor;
    private final StringHttpMessageConverter stringHttpMessageConverter;
    private final RestTemplateBuilder restTemplateBuilder;
    private final RestTemplateConfiguration restTemplateConfiguration;

    public DynamicProxyTemplateHolderInitializer(ThreadPoolTaskExecutor asyncExecutor,
                                                 RestTemplateBuilder restTemplateBuilder,
                                                 StringHttpMessageConverter stringHttpMessageConverter,
                                                 RestTemplateConfiguration restTemplateConfiguration) {
        this.asyncExecutor = asyncExecutor;
        this.restTemplateBuilder = restTemplateBuilder;
        this.stringHttpMessageConverter = stringHttpMessageConverter;
        this.restTemplateConfiguration = restTemplateConfiguration;
    }

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        asyncExecutor.execute(() -> DynamicProxyTemplateHolder.init(restTemplateBuilder, stringHttpMessageConverter, restTemplateConfiguration));
    }

}
