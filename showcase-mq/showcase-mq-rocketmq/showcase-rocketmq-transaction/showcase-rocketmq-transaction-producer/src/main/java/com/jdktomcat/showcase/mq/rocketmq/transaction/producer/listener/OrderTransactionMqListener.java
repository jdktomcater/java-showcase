package com.jdktomcat.showcase.mq.rocketmq.transaction.producer.listener;

import com.alibaba.fastjson2.JSONObject;
import com.jdktomcat.showcase.mq.rocketmq.transaction.producer.model.WithdrawOrder;
import com.jdktomcat.showcase.mq.rocketmq.transaction.producer.util.ThreadLocalUtil;
import com.jdktomcat.showcase.mq.rocketmq.transaction.producer.web.to.OrderTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;

@Slf4j
@RocketMQTransactionListener(corePoolSize = 3, maximumPoolSize = 10)
public class OrderTransactionMqListener implements RocketMQLocalTransactionListener {

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        String jsonStr = new String((byte[]) msg.getPayload(), StandardCharsets.UTF_8);
        if(arg instanceof Function){
            Function<OrderTO,WithdrawOrder> function = (Function<OrderTO,WithdrawOrder>)arg;
            OrderTO orderTO = JSONObject.parseObject(jsonStr, OrderTO.class);
            try{
                ThreadLocalUtil.WITHDRAW_ORDER_THREAD_LOCAL.set(function.apply(orderTO));
                return RocketMQLocalTransactionState.COMMIT;
            }catch (Exception exception){
                log.error("本地事务发生异常！",exception);
                return  RocketMQLocalTransactionState.ROLLBACK;
            }
        }
        return RocketMQLocalTransactionState.UNKNOWN;
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        log.info("checkLocalTransaction msg:{}",msg.getPayload());
        return RocketMQLocalTransactionState.COMMIT;
    }
}
