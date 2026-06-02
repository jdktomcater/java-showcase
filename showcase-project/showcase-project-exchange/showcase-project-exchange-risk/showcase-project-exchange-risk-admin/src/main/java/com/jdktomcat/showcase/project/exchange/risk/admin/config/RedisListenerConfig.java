package com.jdktomcat.showcase.project.exchange.risk.admin.config;

import com.jdktomcat.showcase.project.exchange.risk.admin.redis.RiskRuleRefreshSubscriber;
import com.jdktomcat.showcase.project.exchange.risk.common.redis.RiskRuleRedisKeys;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
@EnableConfigurationProperties(RiskRuleRedisProperties.class)
public class RedisListenerConfig {

    @Bean
    @ConditionalOnProperty(name = "exchange.risk.redis.enabled", havingValue = "true", matchIfMissing = true)
    RedisMessageListenerContainer riskRuleRedisListenerContainer(
            RedisConnectionFactory connectionFactory,
            RiskRuleRefreshSubscriber refreshSubscriber) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(refreshSubscriber, new ChannelTopic(RiskRuleRedisKeys.REFRESH_CHANNEL));
        return container;
    }
}
