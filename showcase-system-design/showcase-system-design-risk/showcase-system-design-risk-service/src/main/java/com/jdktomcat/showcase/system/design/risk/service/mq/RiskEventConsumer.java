package com.jdktomcat.showcase.system.design.risk.service.mq;

import com.alibaba.fastjson2.JSON;
import com.jdktomcat.showcase.system.design.risk.service.domain.RiskEvent;
import com.jdktomcat.showcase.system.design.risk.service.domain.RiskLevel;
import com.jdktomcat.showcase.system.design.risk.service.service.BlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * Async deep-analysis consumer for risk events.
 *
 * <p>Consumer group: {@code risk-analysis}
 * <p>Topic: {@code risk.events}
 *
 * <p>Responsibilities:
 * <ol>
 *   <li><b>BLOCK events</b> → trigger automatic account freeze (call account-service via Feign,
 *       or publish a {@code account.freeze} command topic).</li>
 *   <li><b>REVIEW events</b> → create a manual review task in the compliance queue
 *       (insert into {@code compliance_task} table or call compliance-service).</li>
 *   <li><b>Pattern aggregation</b> → update per-user risk score in a time-series store
 *       (e.g., ClickHouse, Druid) for long-window ML features.</li>
 *   <li><b>Alert routing</b> → send Slack/PagerDuty alerts for BLOCK events involving
 *       large amounts or sanctions hits.</li>
 * </ol>
 *
 * <p>Manual-ack mode ({@code AckMode.MANUAL_IMMEDIATE}) is used to guarantee
 * at-least-once processing: the offset is committed only after all handlers succeed.
 * Combined with idempotent handlers (upsert by requestId), this gives exactly-once
 * effective semantics.
 *
 * <p>Interview talking point: why Kafka over a direct synchronous call?
 * <ul>
 *   <li>Decouples the hot path from slow downstream operations (freeze, alert).</li>
 *   <li>Events are durable — if the compliance service is down, events queue up
 *       and are replayed automatically when it recovers.</li>
 *   <li>Multiple independent consumers can process the same event (fan-out)
 *       without modifying the producer.</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RiskEventConsumer {

    private final BlacklistService blacklistService;

    @KafkaListener(
            topics       = RiskEventProducer.TOPIC,
            groupId      = "risk-analysis",
            concurrency  = "4"
    )
    public void consume(ConsumerRecord<String, String> record, Acknowledgment ack) {
        RiskEvent event;
        try {
            event = JSON.parseObject(record.value(), RiskEvent.class);
        } catch (Exception e) {
            log.error("Failed to deserialise risk event offset={}", record.offset(), e);
            ack.acknowledge();
            return;
        }

        try {
            handleEvent(event);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error handling risk event requestId={} level={}",
                    event.getRequestId(), event.getRiskLevel(), e);
            // Do NOT ack → Kafka will redeliver after max.poll.interval.ms.
            // In production, add a dead-letter topic after N retries.
        }
    }

    private void handleEvent(RiskEvent event) {
        log.info("Processing risk event requestId={} userId={} level={} score={}",
                event.getRequestId(), event.getUserId(),
                event.getRiskLevel(), event.getRiskScore());

        switch (event.getRiskLevel()) {
            case BLOCK -> handleBlock(event);
            case REVIEW -> handleReview(event);
            default -> { /* PASS events are not published */ }
        }
    }

    /**
     * BLOCK: auto-freeze account, alert on-call team.
     *
     * In production: call account-service.freeze(userId) via Feign with circuit breaker.
     * Here we add to blacklist as a simplified stand-in.
     */
    private void handleBlock(RiskEvent event) {
        log.warn("BLOCK event: userId={} triggeredRules={} reason={}",
                event.getUserId(), event.getTriggeredRules(), event.getReason());

        // Temporarily blacklist user to prevent further transactions during investigation
        blacklistService.addUser(event.getUserId());

        // TODO: call account-service to freeze, create compliance alert
    }

    /**
     * REVIEW: create a compliance task for manual review.
     *
     * In production: POST to compliance-service, or write to compliance_task table.
     */
    private void handleReview(RiskEvent event) {
        log.info("REVIEW event queued for manual review: userId={} score={} rules={}",
                event.getUserId(), event.getRiskScore(), event.getTriggeredRules());

        // TODO: insert compliance_task record, notify compliance officer
    }
}
