package com.jdktomcat.showcase.limiter.redis.token.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Slf4j
@Configuration
@Data
public class RedissonConfig {

    /**
     * redis地址
     */
    @Value("${spring.redis.redission.address}")
    private String address;

    /**
     * 数据库
     */
    @Value("${spring.redis.redission.database}")
    private int database;


    /**
     * 单机配置
     *
     * @return redisson客户端
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient singleRedisson() throws IOException {
        // 创建配置
        Config config = new Config();
        config.setCodec(StringCodec.INSTANCE);
        config.setTransportMode(TransportMode.NIO);
        config.useSingleServer().setAddress(address).setDatabase(database);
        return Redisson.create(config);
    }
}
