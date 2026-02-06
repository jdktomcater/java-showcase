package jdktomcat.showcase.mq.rocketmq.pressure.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class TestMessageProducer {

    private final TransactionMQProducer producer;

    private final AtomicLong messageCounter = new AtomicLong(0);

    public TestMessageProducer(TransactionMQProducer producer) {
        this.producer = producer;
    }

    /**
     * 发送测试消息
     */
    public void sendTestMessages(String topic, String tag, int count, int messageSize) {
        for (int i = 0; i < count; i++) {
            String messageBody = generateTestMessage(messageSize);
            String keys = String.format("test-msg-%d", messageCounter.incrementAndGet());

            Message msg = new Message(topic, tag, keys, messageBody.getBytes());

            try {
                SendResult result = producer.send(msg);
                if (result.getSendStatus() == SendStatus.SEND_OK) {
                    log.debug("Sent message: {}", keys);
                } else {
                    log.warn("Failed to send message: {}", keys);
                }
            } catch (Exception e) {
                log.error("Exception sending message: {}", keys, e);
            }
        }
    }

    private String generateTestMessage(int size) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            sb.append((char) (random.nextInt(26) + 'a'));
        }
        return sb.toString();
    }

}
