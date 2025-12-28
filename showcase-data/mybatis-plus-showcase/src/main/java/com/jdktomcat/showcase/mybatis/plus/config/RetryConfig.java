package com.jdktomcat.showcase.mybatis.plus.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Data
@ConfigurationProperties(prefix = "retry")
public class RetryConfig {
    /**
     * 配置
     */
    private Map<String,Integer> config;

}
