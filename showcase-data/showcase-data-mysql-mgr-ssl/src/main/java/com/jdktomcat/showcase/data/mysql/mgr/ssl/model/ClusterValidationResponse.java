package com.jdktomcat.showcase.data.mysql.mgr.ssl.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public class ClusterValidationResponse {

    private final OffsetDateTime inspectedAt;
    private final boolean valid;
    private final int expectedNodeCount;
    private final int reachableNodeCount;
    private final List<CheckResult> checks;
    private final MybatisPlusProbeResult mybatisPlusProbe;
    private final List<ProxyValidationResult> proxies;
    private final List<NodeValidationResult> nodes;
    private final Map<String, String> hints;

    public ClusterValidationResponse(
            OffsetDateTime inspectedAt,
            boolean valid,
            int expectedNodeCount,
            int reachableNodeCount,
            List<CheckResult> checks,
            MybatisPlusProbeResult mybatisPlusProbe,
            List<ProxyValidationResult> proxies,
            List<NodeValidationResult> nodes,
            Map<String, String> hints
    ) {
        this.inspectedAt = inspectedAt;
        this.valid = valid;
        this.expectedNodeCount = expectedNodeCount;
        this.reachableNodeCount = reachableNodeCount;
        this.checks = checks;
        this.mybatisPlusProbe = mybatisPlusProbe;
        this.proxies = proxies;
        this.nodes = nodes;
        this.hints = hints;
    }

    public OffsetDateTime getInspectedAt() {
        return inspectedAt;
    }

    public boolean isValid() {
        return valid;
    }

    public int getExpectedNodeCount() {
        return expectedNodeCount;
    }

    public int getReachableNodeCount() {
        return reachableNodeCount;
    }

    public List<CheckResult> getChecks() {
        return checks;
    }

    public MybatisPlusProbeResult getMybatisPlusProbe() {
        return mybatisPlusProbe;
    }

    public List<ProxyValidationResult> getProxies() {
        return proxies;
    }

    public List<NodeValidationResult> getNodes() {
        return nodes;
    }

    public Map<String, String> getHints() {
        return hints;
    }
}
