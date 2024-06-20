package com.jdktomcat.showcase.rocketmq.transaction.producer.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;

@Slf4j
@RocketMQTransactionListener
public class TransactionMqListener implements RocketMQLocalTransactionListener {

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        log.info("executeLocalTransaction msg:{}",msg.getPayload());
        return RocketMQLocalTransactionState.ROLLBACK;
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        log.info("checkLocalTransaction msg:{}",msg.getPayload());
        return RocketMQLocalTransactionState.COMMIT;
    }
}
