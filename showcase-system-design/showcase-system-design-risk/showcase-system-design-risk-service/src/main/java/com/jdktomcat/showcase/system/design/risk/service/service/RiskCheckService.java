package com.jdktomcat.showcase.system.design.risk.service.service;

import com.alibaba.fastjson2.JSON;
import com.jdktomcat.showcase.system.design.risk.client.dto.RiskCheckRequest;
import com.jdktomcat.showcase.system.design.risk.client.dto.RiskCheckResponse;
import com.jdktomcat.showcase.system.design.risk.client.enums.RiskLevel;
import com.jdktomcat.showcase.system.design.risk.service.domain.RiskCheckRecord;
import com.jdktomcat.showcase.system.design.risk.service.domain.RiskEvent;
import com.jdktomcat.showcase.system.design.risk.service.engine.RiskContext;
import com.jdktomcat.showcase.system.design.risk.service.engine.RiskEngine;
import com.jdktomcat.showcase.system.design.risk.service.limiter.SlidingWindowRateLimiter;
import com.jdktomcat.showcase.system.design.risk.service.mq.RiskEventProducer;
import com.jdktomcat.showcase.system.design.risk.service.repository.RiskCheckRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicDouble;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;

/**
 * Orchestrates the real-time risk check pipeline.
 *
 * <p><b>Hot-path latency budget (target: p99 &lt; 50 ms)</b>
 * <pre>
 *   1. Blacklist checks  (3 × Redis SISMEMBER) ~  3 ms
 *   2. Velocity counts   (3 × Redis Lua)       ~ 10 ms
 *   3. Rule engine       (in-memory)           ~  1 ms
 *   4. Async DB write    (fire-and-forget)        0 ms (off caller thread)
 *   5. Async Kafka pub   (fire-and-forget)        0 ms (off caller thread)
 *   ─────────────────────────────────────────────────
 *   Total                                      ~ 14 ms  (well within budget)
 * </pre>
 *
 * <p>Idempotency design: callers must supply a {@code requestId}; the upstream
 * API gateway (or gRPC interceptor) deduplicates at the network edge using a
 * Redis SET NX with TTL so duplicate requests never reach this service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RiskCheckService {

    private final BlacklistService          blacklistService;
    private final SlidingWindowRateLimiter  rateLimiter;
    private final RiskEngine                riskEngine;
    private final RiskEventProducer         eventProducer;
    private final RiskCheckRecordRepository recordRepository;
    private final RedissonClient            redissonClient;

    public RiskCheckResponse check(RiskCheckRequest req) {
        long start = System.currentTimeMillis();

        RiskContext ctx = buildContext(req);
        RiskEngine.EngineResult result = riskEngine.evaluate(ctx);

        long elapsed = System.currentTimeMillis() - start;

        saveRecordAsync(req, result, elapsed);

        if (result.getRiskLevel() != RiskLevel.PASS) {
            publishEventAsync(req, result);
        }

        log.info("riskCheck requestId={} userId={} event={} level={} score={} elapsed={}ms",
                req.getRequestId(), req.getUserId(), req.getEventType(),
                result.getRiskLevel(), result.getRiskScore(), elapsed);

        return RiskCheckResponse.builder()
                .requestId(req.getRequestId())
                .riskLevel(result.getRiskLevel())
                .riskScore(result.getRiskScore())
                .triggeredRules(result.getTriggeredRules())
                .reason(result.getReason())
                .processingTimeMs(elapsed)
                .build();
    }

    // ── Context enrichment ───────────────────────────────────────────────────

    /**
     * Pre-fetch all I/O-bound signals once so that individual rules are pure
     * in-memory computation (no hidden Redis calls inside a rule).
     */
    private RiskContext buildContext(RiskCheckRequest req) {
        String eventType = req.getEventType();
        Long   userId    = req.getUserId();
        String ip        = req.getUserIp();
        String address   = req.getToAddress();

        boolean userBlacklisted    = blacklistService.isUserBlacklisted(userId);
        boolean ipBlacklisted      = blacklistService.isIpBlacklisted(ip);
        boolean addressBlacklisted = address != null && blacklistService.isAddressBlacklisted(address);

        // countAndRecord registers the current event in the 1-min window and returns count.
        // The 1-hr / 24-hr windows use count() (read-only) to avoid double-counting.
        long velocity1Min = rateLimiter.countAndRecord(
                SlidingWindowRateLimiter.userEventKey(userId, eventType, 60), 60);
        long velocity1Hr  = rateLimiter.count(
                SlidingWindowRateLimiter.userEventKey(userId, eventType, 3600), 3600);
        long velocity24Hr = rateLimiter.count(
                SlidingWindowRateLimiter.userEventKey(userId, eventType, 86400), 86400);

        BigDecimal withdrawal24Hr = get24HrWithdrawalUsd(userId);
        if ("WITHDRAWAL".equals(eventType) && req.getAmountUsd() != null) {
            add24HrWithdrawalUsd(userId, req.getAmountUsd());
            withdrawal24Hr = withdrawal24Hr.add(req.getAmountUsd());
        }

        return RiskContext.builder()
                .requestId(req.getRequestId())
                .userId(userId)
                .userIp(ip)
                .eventType(eventType)
                .assetCode(req.getAssetCode())
                .amount(req.getAmount())
                .amountUsd(req.getAmountUsd())
                .toAddress(address)
                .metadata(req.getMetadata())
                .userBlacklisted(userBlacklisted)
                .ipBlacklisted(ipBlacklisted)
                .addressBlacklisted(addressBlacklisted)
                .velocityCount1Min(velocity1Min)
                .velocityCount1Hr(velocity1Hr)
                .velocityCount24Hr(velocity24Hr)
                .withdrawal24HrUsd(withdrawal24Hr)
                .accountAgeDays(resolveIntMetadata(req, "accountAgeDays", -1))
                .kycLevel(resolveIntMetadata(req, "kycLevel", 0))
                .build();
    }

    // ── 24-hr cumulative withdrawal tracker ──────────────────────────────────

    /**
     * Redis atomic double stores the rolling 24-hr withdrawal USD sum per user.
     * TTL is refreshed on each write so the bucket expires naturally after 24 h of inactivity.
     *
     * <p>Interview note: for higher precision we could use a Redis sorted set of
     * (timestamp → amount) tuples and sum the scores within the window via a Lua script,
     * but an atomic double is a pragmatic choice for this demo given that floating-point
     * precision is acceptable for risk scoring (not accounting).
     */
    private BigDecimal get24HrWithdrawalUsd(Long userId) {
        RAtomicDouble bucket = redissonClient.getAtomicDouble("risk:wd24h:" + userId);
        return BigDecimal.valueOf(bucket.get());
    }

    private void add24HrWithdrawalUsd(Long userId, BigDecimal amount) {
        RAtomicDouble bucket = redissonClient.getAtomicDouble("risk:wd24h:" + userId);
        bucket.addAndGet(amount.doubleValue());
        bucket.expireAsync(Duration.ofHours(24));
    }

    // ── Async side-effects ───────────────────────────────────────────────────

    @Async
    public void saveRecordAsync(RiskCheckRequest req, RiskEngine.EngineResult result, long elapsed) {
        try {
            RiskCheckRecord record = RiskCheckRecord.builder()
                    .requestId(req.getRequestId())
                    .userId(req.getUserId())
                    .userIp(req.getUserIp())
                    .eventType(req.getEventType())
                    .assetCode(req.getAssetCode())
                    .amount(req.getAmount())
                    .amountUsd(req.getAmountUsd())
                    .toAddress(req.getToAddress())
                    .riskLevel(result.getRiskLevel())
                    .riskScore(result.getRiskScore())
                    .triggeredRules(JSON.toJSONString(result.getTriggeredRules()))
                    .reason(result.getReason())
                    .processingTimeMs(elapsed)
                    .build();
            recordRepository.save(record);
        } catch (Exception e) {
            log.error("Failed to persist risk record requestId={}", req.getRequestId(), e);
        }
    }

    @Async
    public void publishEventAsync(RiskCheckRequest req, RiskEngine.EngineResult result) {
        try {
            RiskEvent event = RiskEvent.of(
                    req.getRequestId(), req.getUserId(), req.getUserIp(),
                    req.getEventType(), req.getAssetCode(),
                    req.getAmount(), req.getAmountUsd(), req.getToAddress(),
                    result.getRiskLevel(), result.getRiskScore(),
                    result.getTriggeredRules(), result.getReason());
            eventProducer.publish(event);
        } catch (Exception e) {
            log.error("Failed to publish risk event requestId={}", req.getRequestId(), e);
        }
    }

    // ── Metadata helpers ─────────────────────────────────────────────────────

    private int resolveIntMetadata(RiskCheckRequest req, String key, int defaultValue) {
        if (req.getMetadata() == null) return defaultValue;
        String v = req.getMetadata().get(key);
        if (v == null) return defaultValue;
        try { return Integer.parseInt(v); } catch (NumberFormatException e) { return defaultValue; }
    }
}
