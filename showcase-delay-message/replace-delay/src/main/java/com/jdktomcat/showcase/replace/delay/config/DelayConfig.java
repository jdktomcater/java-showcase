package com.jdktomcat.showcase.replace.delay.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class DelayConfig {

    /**
     * 延迟MQ消息开关
     */
    @Value("${order.state.delay.mq.enable:false}")
    private boolean enableDelayMq;

    /**
     * 延迟Redis消息开关
     */
    @Value("${order.state.delay.redis.enable:true}")
    private boolean enableDelayRedis;

    /**
     * 时间差
     */
    @Value("${order.state.delay.redis.ttl.time.difference:500}")
    private Long ttlDifferenceTime;
}
