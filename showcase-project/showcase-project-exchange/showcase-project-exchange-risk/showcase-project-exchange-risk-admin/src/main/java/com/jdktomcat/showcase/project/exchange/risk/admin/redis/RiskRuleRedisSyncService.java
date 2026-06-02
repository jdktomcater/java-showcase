package com.jdktomcat.showcase.project.exchange.risk.admin.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jdktomcat.showcase.project.exchange.risk.admin.config.RiskRuleRedisProperties;
import com.jdktomcat.showcase.project.exchange.risk.admin.domain.RiskRuleEntity;
import com.jdktomcat.showcase.project.exchange.risk.admin.repository.RiskRuleRepository;
import com.jdktomcat.showcase.project.exchange.risk.admin.service.RiskRuleConverter;
import com.jdktomcat.showcase.project.exchange.risk.common.dto.RiskRuleDTO;
import com.jdktomcat.showcase.project.exchange.risk.common.enums.RuleStatus;
import com.jdktomcat.showcase.project.exchange.risk.common.redis.RiskRuleRedisKeys;
import com.jdktomcat.showcase.project.exchange.risk.common.redis.RiskRuleRedisSnapshot;
import com.jdktomcat.showcase.project.exchange.risk.common.redis.RiskRuleRedisSyncResult;
import com.jdktomcat.showcase.project.exchange.risk.common.redis.RiskRuleRefreshMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "exchange.risk.redis.enabled", havingValue = "true", matchIfMissing = true)
public class RiskRuleRedisSyncService implements RiskRuleRedisPublisher {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final RiskRuleRepository riskRuleRepository;
    private final RiskRuleConverter converter;
    private final RiskRuleRedisProperties redisProperties;

    public RiskRuleRedisSyncResult syncAll(String changeType, String ruleCode, String operator) {
        if (!redisProperties.isEnabled()) {
            return RiskRuleRedisSyncResult.builder()
                    .changeType(changeType)
                    .published(false)
                    .message("Redis sync disabled by configuration")
                    .build();
        }

        try {
            List<RiskRuleDTO> enabledRules = loadEnabledRules();
            long version = System.currentTimeMillis();
            RiskRuleRedisSnapshot snapshot = RiskRuleRedisSnapshot.builder()
                    .version(version)
                    .rules(enabledRules)
                    .build();

            stringRedisTemplate.opsForValue().set(
                    RiskRuleRedisKeys.ENABLED_SNAPSHOT,
                    objectMapper.writeValueAsString(snapshot));
            stringRedisTemplate.opsForValue().set(
                    RiskRuleRedisKeys.VERSION,
                    String.valueOf(version));

            refreshByCodeHash(enabledRules);

            RiskRuleRefreshMessage refreshMessage = RiskRuleRefreshMessage.builder()
                    .version(version)
                    .changeType(changeType)
                    .ruleCode(ruleCode)
                    .operator(operator)
                    .occurredAt(Instant.now().toString())
                    .build();
            stringRedisTemplate.convertAndSend(
                    RiskRuleRedisKeys.REFRESH_CHANNEL,
                    objectMapper.writeValueAsString(refreshMessage));

            log.info("Risk rules synced to Redis: version={}, count={}, changeType={}, ruleCode={}",
                    version, enabledRules.size(), changeType, ruleCode);

            return RiskRuleRedisSyncResult.builder()
                    .version(version)
                    .ruleCount(enabledRules.size())
                    .changeType(changeType)
                    .published(true)
                    .message("Redis snapshot updated and refresh event published")
                    .build();
        } catch (Exception ex) {
            log.error("Failed to sync risk rules to Redis", ex);
            return RiskRuleRedisSyncResult.builder()
                    .changeType(changeType)
                    .published(false)
                    .message("Redis sync failed: " + ex.getMessage())
                    .build();
        }
    }

    public Optional<RiskRuleRedisSnapshot> loadSnapshotFromRedis() {
        if (!redisProperties.isEnabled()) {
            return Optional.empty();
        }
        String json = stringRedisTemplate.opsForValue().get(RiskRuleRedisKeys.ENABLED_SNAPSHOT);
        if (json == null || json.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(json, RiskRuleRedisSnapshot.class));
        } catch (Exception ex) {
            log.warn("Failed to parse Redis risk rule snapshot", ex);
            return Optional.empty();
        }
    }

    public RiskRuleRedisSyncResult getCurrentVersion() {
        if (!redisProperties.isEnabled()) {
            return RiskRuleRedisSyncResult.builder()
                    .published(false)
                    .message("Redis sync disabled by configuration")
                    .build();
        }

        String versionText = stringRedisTemplate.opsForValue().get(RiskRuleRedisKeys.VERSION);
        if (versionText == null || versionText.isBlank()) {
            return RiskRuleRedisSyncResult.builder()
                    .published(false)
                    .message("No Redis snapshot found")
                    .build();
        }

        Optional<RiskRuleRedisSnapshot> snapshot = loadSnapshotFromRedis();
        return RiskRuleRedisSyncResult.builder()
                .version(Long.parseLong(versionText))
                .ruleCount(snapshot.map(s -> s.getRules() == null ? 0 : s.getRules().size()).orElse(0))
                .published(true)
                .message("Redis snapshot available")
                .build();
    }

    private List<RiskRuleDTO> loadEnabledRules() {
        return riskRuleRepository.findByStatusOrderByPriorityAsc(
                        RuleStatus.ENABLED.getCode(),
                        PageRequest.of(0, 1000, Sort.by(Sort.Direction.ASC, "priority", "id")))
                .getContent()
                .stream()
                .map(converter::toDto)
                .toList();
    }

    private void refreshByCodeHash(List<RiskRuleDTO> enabledRules) throws Exception {
        stringRedisTemplate.delete(RiskRuleRedisKeys.BY_CODE_HASH);
        if (enabledRules.isEmpty()) {
            return;
        }

        Map<String, String> entries = new HashMap<>(enabledRules.size());
        for (RiskRuleDTO rule : enabledRules) {
            entries.put(rule.getRuleCode(), objectMapper.writeValueAsString(rule));
        }
        stringRedisTemplate.opsForHash().putAll(RiskRuleRedisKeys.BY_CODE_HASH, entries);
    }
}
