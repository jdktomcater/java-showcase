package com.jdktomcat.showcase.nacos.application.config;

import com.alibaba.nacos.shaded.javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class NacosEnvPostProcessor  implements EnvironmentPostProcessor, ApplicationListener<ApplicationEvent> {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
//        environment.getSystemEnvironment().put("rocketmq.consumer.active","on");
//        log.info("user project update environment properties");
    }

    @Override
    public void onApplicationEvent(@Nullable ApplicationEvent event) {
    }
}
