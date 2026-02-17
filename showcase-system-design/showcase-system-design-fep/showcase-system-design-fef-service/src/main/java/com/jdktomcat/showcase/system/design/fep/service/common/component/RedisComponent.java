package com.jdktomcat.showcase.system.design.fep.service.common.component;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

/**
 * redis component
 */
@Component
@Slf4j
public class RedisComponent {

    /**
     * redisson client
     */
    private final RedissonClient redissonClient;

    public RedisComponent(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * check the idempotent of request id
     *
     * @param requestId request id
     * @return true:yes false:no
     */
    public boolean idempotent(String requestId) {
//        redissonClient.ge
        return true;
    }


}
