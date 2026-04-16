package com.jdktomcat.showcase.data.mysql.mgr.ssl.model;

import java.util.List;
import java.util.Map;

public class NodeValidationResult {

    private final String nodeName;
    private final String jdbcUrl;
    private final boolean reachable;
    private final boolean valid;
    private final List<CheckResult> checks;
    private final Map<String, String> variables;
    private final Map<String, String> sslSession;
    private final Integer probeTableRows;
    private final ClusterMemberView localMember;
    private final List<ClusterMemberView> clusterMembers;
    private final RecoveryChannelView recoveryChannel;
    private final String errorMessage;

    public NodeValidationResult(
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
        this.nodeName = nodeName;
        this.jdbcUrl = jdbcUrl;
        this.reachable = reachable;
        this.valid = valid;
        this.checks = checks;
        this.variables = variables;
        this.sslSession = sslSession;
        this.probeTableRows = probeTableRows;
        this.localMember = localMember;
        this.clusterMembers = clusterMembers;
        this.recoveryChannel = recoveryChannel;
        this.errorMessage = errorMessage;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public boolean isReachable() {
        return reachable;
    }

    public boolean isValid() {
        return valid;
    }

    public List<CheckResult> getChecks() {
        return checks;
    }

    public Map<String, String> getVariables() {
        return variables;
    }

    public Map<String, String> getSslSession() {
        return sslSession;
    }

    public Integer getProbeTableRows() {
        return probeTableRows;
    }

    public ClusterMemberView getLocalMember() {
        return localMember;
    }

    public List<ClusterMemberView> getClusterMembers() {
        return clusterMembers;
    }

    public RecoveryChannelView getRecoveryChannel() {
        return recoveryChannel;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
