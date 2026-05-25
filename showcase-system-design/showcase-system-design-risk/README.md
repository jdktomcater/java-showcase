# Real-Time Risk Control Service

A production-grade **real-time risk control system** implemented with **Spring Boot 3**, designed to demonstrate how crypto-exchange risk systems (e.g. Binance) protect against fraud, AML violations, and account takeover at scale.

---

## Architecture Overview

```
Client Request
  → API Gateway (mTLS / JWT auth)
    → Risk Service  (synchronous, p99 < 50 ms)
        → BlacklistService   (Redis SET SISMEMBER × 3)
        → SlidingWindowRateLimiter  (Redis Lua sorted-set)
        → RiskEngine  (in-memory rule chain)
            → BlacklistRule         (priority 1,  terminal)
            → VelocityRule          (priority 10)
            → AmountThresholdRule   (priority 20, AML CTR)
            → StructuringDetectionRule (priority 30, AML smurfing)
            → NewAccountRule        (priority 40)
        → [async] MySQL audit write
        → [async] Kafka → risk.events
                            → RiskEventConsumer
                                → Account freeze (BLOCK)
                                → Compliance task (REVIEW)
                                → ML feature pipeline
```

---

## Key Design Decisions

### 1. Hot-Path Latency Budget (p99 < 50 ms)

| Step | I/O | Latency |
|------|-----|---------|
| Blacklist check | 3 × Redis SISMEMBER | ~3 ms |
| Velocity counts | 3 × Redis Lua (sliding window) | ~10 ms |
| Rule evaluation | In-memory only | ~1 ms |
| DB write | **Async** (off caller thread) | 0 ms |
| Kafka publish | **Async** (fire-and-forget) | 0 ms |
| **Total** | | **~14 ms** |

Zero DB reads on the critical path.  All enrichment signals are pre-fetched from Redis.

### 2. Sliding-Window Rate Limiter

Uses a Redis **sorted set** per `(userId, eventType, windowSeconds)` with an **atomic Lua script** that in a single round trip:
1. Evicts entries older than the window (`ZREMRANGEBYSCORE`)
2. Records the current request (`ZADD now member`)
3. Refreshes TTL (`EXPIRE`)
4. Returns the count (`ZCARD`)

Why sliding window over fixed window? Fixed window allows 2× the limit at the boundary (e.g. 10 req at 00:59:59 and 10 more at 01:00:01 = 20 in 2 seconds). The sliding window has no boundary-burst vulnerability.

### 3. Rule Engine — Strategy + Chain of Responsibility

```
RiskRule (interface)
  ├── priority()       lower = runs first
  ├── name()           for audit logs
  └── evaluate(ctx) → RuleResult
        ├── PASS        (score contribution = 0)
        ├── REVIEW      (score contribution N, non-terminal)
        └── BLOCK       (score contribution 100, terminal = true)
```

Rules are Spring `@Component` beans auto-collected via `List<RiskRule>` injection.  Adding a new rule requires only creating a new `@Component` class — the engine discovers it automatically.

**Score aggregation:**
- `score < 50`  → PASS
- `50 ≤ score < 80` → REVIEW
- `score ≥ 80` or any terminal BLOCK → BLOCK

### 4. AML Compliance Rules

| Rule | Trigger | Level |
|------|---------|-------|
| `AmountThresholdRule` | Single withdrawal ≥ $100K USD | BLOCK |
| `AmountThresholdRule` | Single withdrawal ≥ $10K USD (CTR) | REVIEW |
| `AmountThresholdRule` | Cumulative 24-hr withdrawal ≥ $50K | REVIEW |
| `StructuringDetectionRule` | ≥3 withdrawals in 24 hr, each < $10K, cumulative ≥ $8K | REVIEW |
| `NewAccountRule` | Account < 1 day old, withdrawal ≥ $500 | BLOCK |

### 5. Blacklist — O(1) Redis SET

Three Redis SETs:
- `risk:blacklist:user` — platform user IDs
- `risk:blacklist:ip` — source IPs
- `risk:blacklist:address` — on-chain addresses (sanctions / OFAC)

For even lower latency: layer a **Bloom filter** (Guava, false-positive rate 0.01%) in front of Redis.  Eliminates ~99.99% of Redis round trips for clean traffic at ~1.2 MB per million entries.

### 6. Kafka Event Pipeline

```
BLOCK  → auto-freeze account + PagerDuty alert
REVIEW → compliance_task table + Slack notification
ALL    → ML feature store (ClickHouse / Druid) for long-window fraud detection
```

Manual-ack Kafka consumer (`AckMode.MANUAL_IMMEDIATE`) + idempotent handlers (upsert by `requestId`) = effectively-exactly-once semantics.

---

## API Reference

### POST `/api/v1/risk/check`

Request:
```json
{
  "requestId": "uuid-v4",
  "userId": 123456,
  "userIp": "1.2.3.4",
  "eventType": "WITHDRAWAL",
  "assetCode": "USDT",
  "amount": "9500.000000",
  "amountUsd": "9500.00",
  "toAddress": "0xABCDEF...",
  "metadata": {
    "accountAgeDays": "15",
    "kycLevel": "1"
  }
}
```

Response:
```json
{
  "requestId": "uuid-v4",
  "riskLevel": "REVIEW",
  "riskScore": 50,
  "triggeredRules": ["StructuringDetectionRule"],
  "reason": "Potential structuring: 3 withdrawals in 24hr, cumulative $27500.00 USD...",
  "processingTimeMs": 14
}
```

### Blacklist Management

```
POST   /api/v1/risk/blacklist/user/{userId}
DELETE /api/v1/risk/blacklist/user/{userId}
POST   /api/v1/risk/blacklist/ip/{ip}
POST   /api/v1/risk/blacklist/address/{address}
DELETE /api/v1/risk/blacklist/address/{address}
```

---

## How to Run

```bash
# Start infrastructure
docker-compose up -d redis kafka mysql

# Run the service
mvn -pl showcase-system-design/showcase-system-design-risk/showcase-system-design-risk-service \
    spring-boot:run
```

---

## Interview Q&A Cheat Sheet

**Q: How do you ensure < 50 ms p99?**
All hot-path reads are Redis.  DB writes and Kafka publishes are async (fire-and-forget on a Spring `@Async` thread pool).  Rule evaluation is pure in-memory arithmetic.

**Q: What happens if Redis is down?**
Circuit breaker (Resilience4j) detects the failure.  Fallback: fail-open for the blacklist (assume clean), apply conservative velocity defaults.  Never let a Redis failure block all transactions — the cost of false negatives is lower than a full outage.

**Q: How do you prevent duplicate processing?**
`requestId` is the idempotency key.  The DB has a `UNIQUE KEY` on `request_id`.  A Redis `SET NX` at the API gateway prevents duplicate requests from reaching the service at all.

**Q: How do you scale this horizontally?**
The service is stateless — deploy N replicas behind a load balancer.  Redis Cluster handles distributed rate limiting.  Kafka consumer groups auto-rebalance partitions across N consumer instances.

**Q: How do you add a new rule without downtime?**
Create a new `@Component` implementing `RiskRule`.  Deploy via rolling restart.  Because the engine sorts by priority at startup, the new rule slots in automatically.  For truly zero-downtime rule changes, store rule configs in Nacos/Redis and reload via `@RefreshScope`.
