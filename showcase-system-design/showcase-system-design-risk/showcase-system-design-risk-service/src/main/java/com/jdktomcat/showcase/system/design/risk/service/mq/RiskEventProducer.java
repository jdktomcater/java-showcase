package com.jdktomcat.showcase.system.design.risk.service.mq;

import com.alibaba.fastjson2.JSON;
import com.jdktomcat.showcase.system.design.risk.service.domain.RiskEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Publishes risk events to Kafka for async downstream processing.
 *
 * <p>Topic: {@code risk.events}
 * <p>Partition key: {@code userId} (string) — ensures all events for the same user
 * land on the same partition, preserving temporal ordering per user.
 *
 * <p>Interview talking point: we use {@code send()} (async, non-blocking) here because:
 * <ul>
 *   <li>The primary risk decision has already been returned to the caller.</li>
 *   <li>Kafka publish failure must not cause the risk API to error (defence-in-depth).</li>
 *   <li>Producer-side idempotence ({@code enable.idempotence=true}) prevents duplicates
 *       on retry, so fire-and-forget is safe from a correctness standpoint.</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RiskEventProducer {

    public static final String TOPIC = "risk.events";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publish(RiskEvent event) {
        String payload = JSON.toJSONString(event);
        String partitionKey = String.valueOf(event.getUserId());
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC, partitionKey, payload);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish risk event requestId={} userId={}", event.getRequestId(), event.getUserId(), ex);
            } else {
                log.debug("Risk event published requestId={} partition={} offset={}",
                        event.getRequestId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
