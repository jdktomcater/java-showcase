package com.jdktomcat.demo.snowflake.redis.component;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisComponent {

    @Autowired
    private RedissonClient redissonClient;

    private static final String SNOWFLAKE_SET_KEY = "SNOWFLAKE_SET_KEY";

    public boolean addSet(Long id){
        RSet<Long> set = redissonClient.getSet(SNOWFLAKE_SET_KEY+(id % 100));
        return set.add(id);
    }
}
