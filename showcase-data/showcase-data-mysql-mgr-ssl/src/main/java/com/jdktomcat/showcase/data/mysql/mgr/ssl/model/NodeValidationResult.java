package com.jdktomcat.showcase.data.mysql.mgr.ssl.model;

import java.util.List;
import java.util.Map;

public record NodeValidationResult(
        String nodeName,
        String jdbcUrl,
        boolean reachable,
        boolean valid,
        List<CheckResult> checks,
        Map<String, String> variables,
        Map<String, String> sslSession,
        Integer probeTableRows,
        ClusterMemberView localMember,
        List<ClusterMemberView> clusterMembers,
        RecoveryChannelView recoveryChannel,
        String errorMessage
) {
}
