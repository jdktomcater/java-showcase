package com.jdktomcat.demo.redis.template.component.lock;

import lombok.Getter;

/**
 *
 *
 * @author luxun
 */

@Getter
public enum RedisLockDef {

    /**
     * 商户锁
     */
    MERCHANT_BALANCE_LOCK("typy:lock:merchantBalanceSum", 8, 10);

    private final String key;

    private final int waitTime;

    private final int leaseTime;

    RedisLockDef(String key, int waitTime, int leaseTime) {
        this.key = key;
        this.waitTime = waitTime;
        this.leaseTime = leaseTime;
    }
}
