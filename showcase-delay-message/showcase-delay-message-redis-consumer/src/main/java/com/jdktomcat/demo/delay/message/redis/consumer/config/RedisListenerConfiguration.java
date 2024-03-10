package com.jdktomcat.demo.delay.message.redis.consumer.config;

import com.jdktomcat.demo.delay.message.redis.consumer.listener.RedisKeyExpiredListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;

/**
 * @author ZF-Timmy
 * @version V1.0
 * @description 类描述：redis监听配置
 * @date 2023/11/24
 */
@Configuration
public class RedisListenerConfiguration {

    @Value("${spring.redis.listen-pattern}")
    public String pattern;

    @Bean
    public RedisMessageListenerContainer listenerContainer(RedisConnectionFactory redisConnection) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnection);
        Topic topic = new PatternTopic(this.pattern);
        container.addMessageListener(new RedisKeyExpiredListener(), topic);
        return container;
    }
}
