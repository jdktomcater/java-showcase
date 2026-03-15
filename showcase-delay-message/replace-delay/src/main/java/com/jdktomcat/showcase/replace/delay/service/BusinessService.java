package com.jdktomcat.showcase.replace.delay.service;

import com.jdktomcat.showcase.replace.delay.component.MessageProducerComponent;
import com.jdktomcat.showcase.replace.delay.component.RedisComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.jdktomcat.showcase.replace.delay.constant.ReplaceDelayConstant.*;

@Slf4j
@Service
public class BusinessService {

    /**
     * redis服务组件
     */
    @Autowired
    private RedisComponent redisComponent;

    /**
     * 消息生产者组件
     */
    @Autowired
    private MessageProducerComponent messageProducerComponent;

    /**
     * 锁定订单
     *
     * @param merchantId 商户标识
     * @param userId     用户标识
     */
    public void lock(String merchantId, String userId, BigDecimal amount) {
//        String maUserId = UAMT_PREFIX + merchantId + "_" + userId;

        String mUserId = UEWF_PREFIX + merchantId + "_" + userId;
        long value = redisComponent.incr(mUserId);
        boolean result = redisComponent.expire(mUserId, 45);
        log.info("lock value:{} expire 2mins:{}", value, result);
        // 发送延迟消息
        messageProducerComponent.sendDelayMessage(DELAY_TAG_UEWF, mUserId, mUserId + KEY_VALUE_SPLIT + value, 45);
    }

    /**
     * 解锁
     *
     * @param merchantId 商户标识
     * @param userId     用户标识
     */
    public void unlock(String merchantId, String userId) {
        boolean result = redisComponent.del(UEWF_PREFIX + merchantId + "_" + userId);
        log.info("unlock result:{}", result);
    }


    /**
     * 下单逻辑
     *
     * @param merchantId 商户标识
     * @param userId 用户标识
     * @return 下单成功
     */
    public boolean order(String merchantId, String userId) {
        return redisComponent.exist(UEWF_PREFIX + merchantId + "_" + userId);
    }
}
