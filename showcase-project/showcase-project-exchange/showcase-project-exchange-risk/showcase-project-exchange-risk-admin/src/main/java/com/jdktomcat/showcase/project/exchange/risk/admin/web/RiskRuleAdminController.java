package com.jdktomcat.showcase.project.exchange.risk.admin.web;

import com.jdktomcat.showcase.project.exchange.risk.admin.redis.RiskRuleRedisPublisher;
import com.jdktomcat.showcase.project.exchange.risk.admin.service.RiskRuleAdminService;
import com.jdktomcat.showcase.project.exchange.risk.common.dto.*;
import com.jdktomcat.showcase.project.exchange.risk.common.redis.RiskRuleRedisSnapshot;
import com.jdktomcat.showcase.project.exchange.risk.common.redis.RiskRuleRedisSyncResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin REST API for exchange risk rule management.
 */
@RestController
@RequestMapping("/api/admin/risk")
@RequiredArgsConstructor
public class RiskRuleAdminController {

    private final RiskRuleAdminService riskRuleAdminService;
    private final RiskRuleRedisPublisher redisSyncService;

    @GetMapping("/rules")
    public ApiResult<PageResult<RiskRuleDTO>> listRules(
            @RequestParam(value = "ruleType", required = false) String ruleType,
            @RequestParam(value = "scope", required = false) String scope,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        return ApiResult.ok(riskRuleAdminService.listRules(ruleType, scope, status, keyword, page, size));
    }

    @GetMapping("/rules/{id}")
    public ApiResult<RiskRuleDTO> getRule(@PathVariable("id") Long id) {
        return ApiResult.ok(riskRuleAdminService.getRule(id));
    }

    @PostMapping("/rules")
    public ApiResult<RiskRuleDTO> createRule(@Valid @RequestBody RiskRuleCreateRequest request) {
        return ApiResult.ok(riskRuleAdminService.createRule(request));
    }

    @PutMapping("/rules/{id}")
    public ApiResult<RiskRuleDTO> updateRule(@PathVariable("id") Long id,
                                             @Valid @RequestBody RiskRuleUpdateRequest request) {
        return ApiResult.ok(riskRuleAdminService.updateRule(id, request));
    }

    @PatchMapping("/rules/{id}/status")
    public ApiResult<RiskRuleDTO> updateStatus(@PathVariable("id") Long id,
                                               @Valid @RequestBody RiskRuleStatusRequest request) {
        return ApiResult.ok(riskRuleAdminService.updateStatus(id, request));
    }

    @DeleteMapping("/rules/{id}")
    public ApiResult<Void> deleteRule(@PathVariable("id") Long id,
                                      @RequestParam(value = "operator", defaultValue = "admin") String operator) {
        riskRuleAdminService.deleteRule(id, operator);
        return ApiResult.ok();
    }

    @GetMapping("/rules/{id}/change-logs")
    public ApiResult<PageResult<RiskRuleChangeLogDTO>> listChangeLogs(
            @PathVariable("id") Long id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        return ApiResult.ok(riskRuleAdminService.listChangeLogs(id, page, size));
    }

    @GetMapping("/rule-types")
    public ApiResult<List<RiskRuleTypeInfo>> listRuleTypes() {
        return ApiResult.ok(riskRuleAdminService.listRuleTypes());
    }

    /**
     * Internal read API for matching / order gateway to pull enabled rules at startup or via refresh.
     */
    @GetMapping("/rules/enabled")
    public ApiResult<List<RiskRuleDTO>> listEnabledRules() {
        return ApiResult.ok(riskRuleAdminService.listEnabledRules());
    }

    @PostMapping("/rules/sync-redis")
    public ApiResult<RiskRuleRedisSyncResult> syncRedis(
            @RequestParam(value = "operator", defaultValue = "admin") String operator) {
        return ApiResult.ok(redisSyncService.syncAll("MANUAL", null, operator));
    }

    @GetMapping("/rules/redis-version")
    public ApiResult<RiskRuleRedisSyncResult> getRedisVersion() {
        return ApiResult.ok(redisSyncService.getCurrentVersion());
    }

    /**
     * Read enabled rules from Redis snapshot — same data consumed by matching / order services.
     */
    @GetMapping("/rules/enabled/cache")
    public ApiResult<RiskRuleRedisSnapshot> getEnabledRulesFromRedis() {
        return redisSyncService.loadSnapshotFromRedis()
                .map(ApiResult::ok)
                .orElseGet(() -> ApiResult.fail("Redis 中暂无规则快照，请先执行 sync-redis"));
    }
}
