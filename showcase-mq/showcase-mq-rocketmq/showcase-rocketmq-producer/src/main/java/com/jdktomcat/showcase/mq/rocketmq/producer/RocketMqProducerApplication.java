package com.jdktomcat.showcase.mq.rocketmq.producer;

import com.alibaba.fastjson.JSONObject;
import com.jdktomcat.showcase.mq.rocketmq.producer.component.MessageProducerComponent;
import com.jdktomcat.showcase.mq.rocketmq.producer.message.DistributeMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootApplication
public class RocketMqProducerApplication implements CommandLineRunner {

    @Autowired
    private MessageProducerComponent messageProducerComponent;


    public static void main(String[] args) {
        SpringApplication.run(RocketMqProducerApplication.class, args);
    }


    private static final String tag = "findDistributionMatchupJob";

    private static final String key = "key";

    @Override
    public void run(String... args) {
        sendDisMessage();
    }


    private void sendDelayMessage() {
        int i = 0;
        while (i++ < 1000000) {
            SendResult sendResult = messageProducerComponent.sendDelayMessage("JSTWZ20240229153150E19M:"+i, key+i);
            log.info("syncSend to sendResult:{}", sendResult);
        }
    }


    private void sendDisMessage() {
        int i = 0;
        while (i++ < 100000) {
            DistributeMessage distributeMessage = new DistributeMessage();
            distributeMessage.setType(tag);
            distributeMessage.setUsername("timmy"+i);
            List<DistributeMessage.WithdrawOrderDto> dataList = new ArrayList<>();
            DistributeMessage.WithdrawOrderDto withdrawOrderDto = new DistributeMessage.WithdrawOrderDto();
            withdrawOrderDto.setOrderIndex(2210171858242079902L);
            distributeMessage.setWithdrawOrderList(dataList);
            SendResult sendResult = messageProducerComponent.sendDisMessage(JSONObject.toJSONString(distributeMessage), key+i);
            log.info("syncSend to sendResult:{}", sendResult);
        }
    }
}

