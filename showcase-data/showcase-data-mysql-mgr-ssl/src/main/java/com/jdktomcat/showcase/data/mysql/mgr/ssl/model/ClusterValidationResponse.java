package com.jdktomcat.showcase.data.mysql.mgr.ssl.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public record ClusterValidationResponse(
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
}
