package com.jdktomcat.demo.rocketmq.producer;

import com.alibaba.fastjson.JSONObject;
import com.jdktomcat.demo.rocketmq.producer.component.MessageProducerComponent;
import com.jdktomcat.demo.rocketmq.producer.message.DistributeMessage;
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

    /**
     * 模拟机器人
     */
    private static final String[] BOTS = {"6774799455:AAFeHKrLxxMRVpBLg8LFj7N62drOxz7b25g", "6347956777:AAGkrZ2ii7AuiKNiO1LflAGy8owYjTg3oWk", "6860056824:AAHoBVhelMHs6KXRHey2ARiKegaYlfiKpwk"};

    /**
     * 模拟群组
     */
    private static final String[] CHATS = {"-4043624377", "-4086688530", "-4095197160"};

    private static final String tag = "findDistributionMatchupJob";

    private static final String key = "key";

    @Override
    public void run(String... args) {
        sendDelayMessage();
    }


    private void sendDelayMessage() {
        int i = 0;
        while (i++ < 1000000) {
            SendResult sendResult = messageProducerComponent.sendDelayMessage("JSTWZ20240229153150E19M:"+i, key+i);
            log.info("syncSend to sendResult:{}", sendResult);
        }
    }


    private void sendDisMessage() throws InterruptedException {
        int i = 0;
        while (i++ < 10000) {
            DistributeMessage distributeMessage = new DistributeMessage();
            distributeMessage.setType(tag);
            distributeMessage.setUsername("timmy"+i);
            List<DistributeMessage.WithdrawOrderDto> dataList = new ArrayList<>();
            DistributeMessage.WithdrawOrderDto withdrawOrderDto = new DistributeMessage.WithdrawOrderDto();
            withdrawOrderDto.setOrderIndex(2210171858242079902L);
            distributeMessage.setWithdrawOrderList(dataList);
            SendResult sendResult = messageProducerComponent.sendDisMessage(JSONObject.toJSONString(distributeMessage), key+i);
            log.info("syncSend to sendResult:{}", sendResult);
            TimeUnit.SECONDS.sleep(1);
        }
    }
}

