package com.jdktomcat.showcase.rocketmq.transaction.producer.component;

import com.alibaba.fastjson2.JSONObject;
import com.jdktomcat.showcase.rocketmq.transaction.producer.model.WithdrawOrder;
import com.jdktomcat.showcase.rocketmq.transaction.producer.util.ThreadLocalUtil;
import com.jdktomcat.showcase.rocketmq.transaction.producer.web.to.OrderTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * @author ZF-Timmy
 * @version V1.0
 * @description 类描述：消息生产者服务组件
 * @date 2024/06/18
 */
@Slf4j
@Component
public class MqProducerComponent {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Value("${showcase.rocketmq.transaction.topic}")
    private String transactionTopic;


    /**
     * 发送事务消息
     *
     * @param orderTO 消息
     * @return 发送结果
     */
    public WithdrawOrder sendTransactionMessage(OrderTO orderTO, Function<OrderTO, WithdrawOrder> function) {
        TransactionSendResult transactionSendResult = rocketMQTemplate.sendMessageInTransaction(transactionTopic, MessageBuilder.withPayload(JSONObject.toJSONString(orderTO)).build(),function);
        log.info("transactionSendResult:{}",transactionSendResult);
        WithdrawOrder withdrawOrder = ThreadLocalUtil.WITHDRAW_ORDER_THREAD_LOCAL.get();
        if(withdrawOrder!=null){
            ThreadLocalUtil.WITHDRAW_ORDER_THREAD_LOCAL.remove();
        }
        return withdrawOrder;
    }
}
