package com.jdktomcat.showcase.system.design.risk.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Real-time Risk Control Service.
 *
 * <p>Designed to mirror production-grade crypto-exchange risk systems (e.g. Binance).
 *
 * <p>Key design pillars:
 * <ul>
 *   <li><b>Low latency</b> – all hot-path I/O goes to Redis (no DB reads on critical path);
 *       target p99 &lt; 50 ms.</li>
 *   <li><b>Rule engine</b> – pluggable Spring-bean rules evaluated in priority order
 *       with short-circuit termination; add new rules without modifying the engine.</li>
 *   <li><b>AML compliance</b> – large-amount detection (CTR) and structuring detection.</li>
 *   <li><b>Async side-effects</b> – DB audit writes and Kafka event publishing are
 *       decoupled from the synchronous response via {@link EnableAsync}.</li>
 *   <li><b>Durability</b> – Kafka events guarantee at-least-once delivery to downstream
 *       processors (account freeze, compliance alerts, ML scoring).</li>
 * </ul>
 */
@SpringBootApplication
@EnableAsync
@EnableKafka
public class RiskServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RiskServiceApplication.class, args);
    }
}
