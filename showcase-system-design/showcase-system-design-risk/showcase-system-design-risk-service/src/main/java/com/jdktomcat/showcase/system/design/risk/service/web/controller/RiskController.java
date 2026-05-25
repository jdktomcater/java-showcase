package com.jdktomcat.showcase.system.design.risk.service.web.controller;

import com.jdktomcat.showcase.system.design.risk.service.service.BlacklistService;
import com.jdktomcat.showcase.system.design.risk.service.service.RiskCheckService;
import com.jdktomcat.showcase.system.design.risk.service.web.dto.RiskCheckRequest;
import com.jdktomcat.showcase.system.design.risk.service.web.dto.RiskCheckResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST API entry point for the real-time risk-check service.
 *
 * <p>All endpoints are internal (service-to-service); they are not exposed directly
 * to the internet and rely on mTLS or API-gateway authentication.
 */
@RestController
@RequestMapping("/api/v1/risk")
@RequiredArgsConstructor
public class RiskController {

    private final RiskCheckService riskCheckService;
    private final BlacklistService blacklistService;

    /**
     * Synchronous risk check — the primary hot-path endpoint.
     *
     * <p>Callers MUST call this before executing any financial operation and
     * MUST respect the returned {@code riskLevel}:
     * <ul>
     *   <li>{@code PASS}   – proceed.</li>
     *   <li>{@code REVIEW} – hold and route to manual review.</li>
     *   <li>{@code BLOCK}  – reject immediately.</li>
     * </ul>
     *
     * <p>Target latency: p99 &lt; 50 ms.
     */
    @PostMapping("/check")
    public ResponseEntity<RiskCheckResponse> check(@Valid @RequestBody RiskCheckRequest request) {
        return ResponseEntity.ok(riskCheckService.check(request));
    }

    // ── Blacklist management (admin / compliance use only) ──────────────────

    @PostMapping("/blacklist/user/{userId}")
    public ResponseEntity<Void> blacklistUser(@PathVariable Long userId) {
        blacklistService.addUser(userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/blacklist/user/{userId}")
    public ResponseEntity<Void> unblacklistUser(@PathVariable Long userId) {
        blacklistService.removeUser(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/blacklist/ip/{ip}")
    public ResponseEntity<Void> blacklistIp(@PathVariable String ip) {
        blacklistService.addIp(ip);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/blacklist/address/{address}")
    public ResponseEntity<Void> blacklistAddress(@PathVariable String address) {
        blacklistService.addAddress(address);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/blacklist/address/{address}")
    public ResponseEntity<Void> unblacklistAddress(@PathVariable String address) {
        blacklistService.removeAddress(address);
        return ResponseEntity.ok().build();
    }
}
