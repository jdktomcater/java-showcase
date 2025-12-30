package com.jdktomcat.showcase.replace.delay.service;

import com.jdktomcat.showcase.replace.delay.component.MessageProducerComponent;
import com.jdktomcat.showcase.replace.delay.component.RedisComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.jdktomcat.showcase.replace.delay.constant.ReplaceDelayConstant.KEY_VALUE_SPLIT;
import static com.jdktomcat.showcase.replace.delay.constant.ReplaceDelayConstant.UEWF_PREFIX;

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
    public void lock(String merchantId, String userId) {
        String mUserId = UEWF_PREFIX + merchantId + "_" + userId;
        long value = redisComponent.incr(mUserId);
        boolean result = redisComponent.expire(mUserId, 60 * 2);
        log.info("lock value:{} expire 2mins:{}", value, result);
        // 延迟消息
        messageProducerComponent.send(mUserId + KEY_VALUE_SPLIT + value, 60 * 2);
    }

    public void unlock(String merchantId, String userId) {

    }

    public void order() {

    }
}
