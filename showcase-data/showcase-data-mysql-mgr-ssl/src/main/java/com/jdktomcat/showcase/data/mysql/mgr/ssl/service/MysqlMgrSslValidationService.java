package com.jdktomcat.showcase.data.mysql.mgr.ssl.service;

import com.jdktomcat.showcase.data.mysql.mgr.ssl.config.MysqlMgrSslProperties;
import com.jdktomcat.showcase.data.mysql.mgr.ssl.model.CheckResult;
import com.jdktomcat.showcase.data.mysql.mgr.ssl.model.ClusterMemberView;
import com.jdktomcat.showcase.data.mysql.mgr.ssl.model.ClusterValidationResponse;
import com.jdktomcat.showcase.data.mysql.mgr.ssl.model.MybatisPlusProbeResult;
import com.jdktomcat.showcase.data.mysql.mgr.ssl.model.NodeValidationResult;
import com.jdktomcat.showcase.data.mysql.mgr.ssl.model.ProxyValidationResult;
import com.jdktomcat.showcase.data.mysql.mgr.ssl.model.RecoveryChannelView;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class MysqlMgrSslValidationService {

    private static final String NODE_OVERVIEW_SQL = """
            SELECT @@hostname AS hostname,
                   @@port AS port,
                   @@version AS version,
                   @@server_uuid AS server_uuid,
                   @@group_replication_ssl_mode AS group_replication_ssl_mode,
                   @@group_replication_recovery_use_ssl AS group_replication_recovery_use_ssl,
                   @@require_secure_transport AS require_secure_transport
            """;

    private static final String LOCAL_MEMBER_SQL = """
            SELECT member_host, member_port, member_state, member_role
            FROM performance_schema.replication_group_members
            WHERE member_id = @@server_uuid
            """;

    private static final String CLUSTER_MEMBERS_SQL = """
            SELECT member_host, member_port, member_state, member_role
            FROM performance_schema.replication_group_members
            ORDER BY member_host, member_port
            """;

    private static final String RECOVERY_CHANNEL_SQL = """
            SELECT CHANNEL_NAME,
                   HOST,
                   PORT,
                   USER AS replication_user,
                   SSL_ALLOWED,
                   TLS_VERSION,
                   SSL_CA_FILE,
                   SSL_VERIFY_SERVER_CERTIFICATE
            FROM performance_schema.replication_connection_configuration
            WHERE CHANNEL_NAME = 'group_replication_recovery'
            """;

    private static final String SSL_STATUS_SQL = """
            SHOW SESSION STATUS
            WHERE Variable_name IN ('Ssl_version', 'Ssl_cipher', 'Ssl_verify_mode')
            """;

    private static final String PROXY_OVERVIEW_SQL = """
            SELECT @@hostname AS hostname,
                   @@port AS port,
                   @@super_read_only AS super_read_only,
                   @@read_only AS read_only
            """;

    private final MysqlMgrSslProperties properties;
    private final MysqlMgrEndpointJdbcService endpointJdbcService;
    private final MybatisPlusProbeValidationService mybatisPlusProbeValidationService;

    public MysqlMgrSslValidationService(
            MysqlMgrSslProperties properties,
            MysqlMgrEndpointJdbcService endpointJdbcService,
            MybatisPlusProbeValidationService mybatisPlusProbeValidationService
    ) {
        this.properties = properties;
        this.endpointJdbcService = endpointJdbcService;
        this.mybatisPlusProbeValidationService = mybatisPlusProbeValidationService;
    }

    public ClusterValidationResponse validateCluster() {
        MybatisPlusProbeResult mybatisPlusProbe = mybatisPlusProbeValidationService.validate();

        List<NodeValidationResult> nodeResults = new ArrayList<>();
        for (MysqlMgrSslProperties.Node node : properties.getNodes()) {
            nodeResults.add(validateNode(node));
        }

        List<ProxyValidationResult> proxyResults = validateProxies();
        boolean proxyEnabled = properties.getProxy() != null && properties.getProxy().isEnabled();

        int expectedNodeCount = properties.getNodes().size();
        int reachableNodeCount = (int) nodeResults.stream().filter(NodeValidationResult::reachable).count();
        boolean allReachable = reachableNodeCount == expectedNodeCount && expectedNodeCount > 0;
        boolean allJdbcEncrypted = nodeResults.stream().allMatch(this::jdbcEncrypted);
        boolean allGroupSslEnabled = nodeResults.stream().allMatch(this::groupSslEnabled);
        boolean allRecoverySslEnabled = nodeResults.stream().allMatch(this::recoverySslEnabled);
        boolean allMembersOnline = nodeResults.stream().allMatch(this::memberOnline);
        boolean clusterSizeMatches = nodeResults.stream()
                .filter(NodeValidationResult::reachable)
                .allMatch(result -> result.clusterMembers() != null && result.clusterMembers().size() == expectedNodeCount);
        boolean readWriteProxyValid = !proxyEnabled || proxyValid(proxyResults, properties.getProxy().getReadWrite().getName());
        boolean readOnlyProxyValid = !proxyEnabled || proxyValid(proxyResults, properties.getProxy().getReadOnly().getName());

        List<CheckResult> clusterChecks = List.of(
                new CheckResult("reachable_nodes", allReachable,
                        "reachable=" + reachableNodeCount + ", expected=" + expectedNodeCount),
                new CheckResult("jdbc_tls_encrypted", allJdbcEncrypted,
                        "every validation connection should expose Ssl_version and Ssl_cipher"),
                new CheckResult("group_replication_ssl_mode", allGroupSslEnabled,
                        "every node should keep group_replication_ssl_mode != DISABLED"),
                new CheckResult("group_replication_recovery_ssl", allRecoverySslEnabled,
                        "every node should keep group_replication_recovery_use_ssl=ON and recovery channel SSL allowed"),
                new CheckResult("member_online", allMembersOnline,
                        "every local member should be ONLINE"),
                new CheckResult("cluster_size", clusterSizeMatches,
                        "every node should see " + expectedNodeCount + " members in replication_group_members"),
                new CheckResult("f5_rw_proxy", readWriteProxyValid,
                        proxyEnabled ? "write proxy should route to a writable primary and keep TLS enabled" : "proxy validation disabled"),
                new CheckResult("f5_ro_proxy", readOnlyProxyValid,
                        proxyEnabled ? "read proxy should route to a readonly secondary and keep TLS enabled" : "proxy validation disabled"),
                new CheckResult("mybatis_plus_probe_rw", mybatisPlusProbe.valid(),
                        mybatisPlusProbe.valid()
                                ? "mybatis-plus updated ssl_probe through routed host " + mybatisPlusProbe.routedHost()
                                : Objects.toString(mybatisPlusProbe.errorMessage(), "mybatis-plus validation failed"))
        );

        Map<String, String> hints = new LinkedHashMap<>();
        hints.put("manual_sql_1", "SHOW STATUS LIKE 'Ssl_%';");
        hints.put("manual_sql_2", "SHOW VARIABLES LIKE 'group_replication%ssl%';");
        hints.put("manual_sql_3", "SELECT * FROM performance_schema.replication_group_members;");
        hints.put("manual_sql_4", "SELECT @@hostname, @@super_read_only, @@read_only;");
        hints.put("manual_sql_5", "SELECT * FROM mgr_demo.ssl_probe WHERE id = 1;");

        boolean valid = clusterChecks.stream().allMatch(CheckResult::passed);
        return new ClusterValidationResponse(
                OffsetDateTime.now(),
                valid,
                expectedNodeCount,
                reachableNodeCount,
                clusterChecks,
                mybatisPlusProbe,
                proxyResults,
                nodeResults,
                hints
        );
    }

    private NodeValidationResult validateNode(MysqlMgrSslProperties.Node node) {
        JdbcTemplate jdbcTemplate = endpointJdbcService.jdbcTemplateForNode(node);
        try {
            Map<String, String> variables = querySingleRow(jdbcTemplate, NODE_OVERVIEW_SQL);
            Map<String, String> sslSession = queryKeyValueRows(jdbcTemplate, SSL_STATUS_SQL, "Variable_name", "Value");
            ClusterMemberView localMember = queryOptionalMember(jdbcTemplate, LOCAL_MEMBER_SQL);
            List<ClusterMemberView> clusterMembers = queryMembers(jdbcTemplate, CLUSTER_MEMBERS_SQL);
            RecoveryChannelView recoveryChannel = queryRecoveryChannel(jdbcTemplate);
            Integer probeTableRows = queryInteger(jdbcTemplate, "SELECT COUNT(*) FROM " + node.getDatabase() + ".ssl_probe");
            boolean recoveryChannelSecure = isRecoveryChannelSecure(localMember, recoveryChannel);

            List<CheckResult> checks = new ArrayList<>();
            checks.add(new CheckResult("jdbc_tls_encrypted", hasText(sslSession.get("Ssl_version")) && hasText(sslSession.get("Ssl_cipher")),
                    "Ssl_version=" + sslSession.get("Ssl_version") + ", Ssl_cipher=" + sslSession.get("Ssl_cipher")));
            checks.add(new CheckResult("group_replication_ssl_mode", !"DISABLED".equalsIgnoreCase(variables.get("group_replication_ssl_mode")),
                    "group_replication_ssl_mode=" + variables.get("group_replication_ssl_mode")));
            checks.add(new CheckResult("group_replication_recovery_use_ssl",
                    isEnabled(variables.get("group_replication_recovery_use_ssl")) && recoveryChannelSecure,
                    "group_replication_recovery_use_ssl=" + variables.get("group_replication_recovery_use_ssl")
                            + ", SSL_ALLOWED=" + recoveryChannelDetail(localMember, recoveryChannel)));
            checks.add(new CheckResult("require_secure_transport", isEnabled(variables.get("require_secure_transport")),
                    "require_secure_transport=" + variables.get("require_secure_transport")));
            checks.add(new CheckResult("local_member_online",
                    localMember != null && "ONLINE".equalsIgnoreCase(localMember.state()),
                    localMember == null ? "no row in replication_group_members for current node"
                            : "state=" + localMember.state() + ", role=" + localMember.role()));
            checks.add(new CheckResult("probe_table_accessible", probeTableRows != null,
                    probeTableRows == null ? "unable to read mgr_demo.ssl_probe" : "mgr_demo.ssl_probe rows=" + probeTableRows));

            boolean valid = checks.stream().allMatch(CheckResult::passed);
            return new NodeValidationResult(
                    node.getName(),
                    buildJdbcUrl(node.getHost(), node.getPort(), node.getDatabase()),
                    true,
                    valid,
                    checks,
                    variables,
                    sslSession,
                    probeTableRows,
                    localMember,
                    clusterMembers,
                    recoveryChannel,
                    null
            );
        } catch (Exception ex) {
            List<CheckResult> checks = List.of(new CheckResult("connectivity", false, ex.getMessage()));
            return new NodeValidationResult(
                    node.getName(),
                    buildJdbcUrl(node.getHost(), node.getPort(), node.getDatabase()),
                    false,
                    false,
                    checks,
                    Map.of(),
                    Map.of(),
                    null,
                    null,
                    List.of(),
                    null,
                    ex.getMessage()
            );
        }
    }

    private List<ProxyValidationResult> validateProxies() {
        if (properties.getProxy() == null || !properties.getProxy().isEnabled()) {
            return List.of();
        }

        List<ProxyValidationResult> results = new ArrayList<>();
        results.add(validateProxy(properties.getProxy().getReadWrite(), true));
        results.add(validateProxy(properties.getProxy().getReadOnly(), false));
        return results;
    }

    private ProxyValidationResult validateProxy(MysqlMgrSslProperties.Endpoint endpoint, boolean expectPrimary) {
        JdbcTemplate jdbcTemplate = endpointJdbcService.jdbcTemplateForEndpoint(endpoint);
        try {
            Map<String, String> serverVariables = querySingleRow(jdbcTemplate, PROXY_OVERVIEW_SQL);
            Map<String, String> sslSession = queryKeyValueRows(jdbcTemplate, SSL_STATUS_SQL, "Variable_name", "Value");
            boolean tlsEnabled = hasText(sslSession.get("Ssl_version")) && hasText(sslSession.get("Ssl_cipher"));
            boolean writablePrimary = isDisabled(serverVariables.get("super_read_only")) && isDisabled(serverVariables.get("read_only"));
            boolean readonlySecondary = isEnabled(serverVariables.get("super_read_only")) || isEnabled(serverVariables.get("read_only"));
            boolean valid = tlsEnabled && (expectPrimary ? writablePrimary : readonlySecondary);
            return new ProxyValidationResult(
                    endpoint.getName(),
                    buildJdbcUrl(endpoint.getHost(), endpoint.getPort(), endpoint.getDatabase()),
                    true,
                    valid,
                    serverVariables.get("hostname"),
                    serverVariables,
                    sslSession,
                    null
            );
        } catch (Exception ex) {
            return new ProxyValidationResult(
                    endpoint.getName(),
                    buildJdbcUrl(endpoint.getHost(), endpoint.getPort(), endpoint.getDatabase()),
                    false,
                    false,
                    null,
                    Map.of(),
                    Map.of(),
                    ex.getMessage()
            );
        }
    }

    private Map<String, String> querySingleRow(JdbcTemplate jdbcTemplate, String sql) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        if (rows.isEmpty()) {
            return Map.of();
        }
        return convertMap(rows.get(0));
    }

    private Map<String, String> queryKeyValueRows(JdbcTemplate jdbcTemplate, String sql, String keyColumn, String valueColumn) {
        Map<String, String> result = new LinkedHashMap<>();
        for (Map<String, Object> row : jdbcTemplate.queryForList(sql)) {
            result.put(Objects.toString(row.get(keyColumn), null), Objects.toString(row.get(valueColumn), null));
        }
        return result;
    }

    private ClusterMemberView queryOptionalMember(JdbcTemplate jdbcTemplate, String sql) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        if (rows.isEmpty()) {
            return null;
        }
        Map<String, Object> row = rows.get(0);
        return new ClusterMemberView(
                Objects.toString(row.get("member_host"), null),
                toInt(row.get("member_port")),
                Objects.toString(row.get("member_state"), null),
                Objects.toString(row.get("member_role"), null)
        );
    }

    private List<ClusterMemberView> queryMembers(JdbcTemplate jdbcTemplate, String sql) {
        List<ClusterMemberView> members = new ArrayList<>();
        for (Map<String, Object> row : jdbcTemplate.queryForList(sql)) {
            members.add(new ClusterMemberView(
                    Objects.toString(row.get("member_host"), null),
                    toInt(row.get("member_port")),
                    Objects.toString(row.get("member_state"), null),
                    Objects.toString(row.get("member_role"), null)
            ));
        }
        return members;
    }

    private RecoveryChannelView queryRecoveryChannel(JdbcTemplate jdbcTemplate) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(RECOVERY_CHANNEL_SQL);
        if (rows.isEmpty()) {
            return null;
        }
        Map<String, Object> row = rows.get(0);
        return new RecoveryChannelView(
                Objects.toString(row.get("CHANNEL_NAME"), null),
                Objects.toString(row.get("HOST"), null),
                row.get("PORT") == null ? null : toInt(row.get("PORT")),
                Objects.toString(row.get("replication_user"), null),
                Objects.toString(row.get("SSL_ALLOWED"), null),
                Objects.toString(row.get("TLS_VERSION"), null),
                Objects.toString(row.get("SSL_CA_FILE"), null),
                Objects.toString(row.get("SSL_VERIFY_SERVER_CERTIFICATE"), null)
        );
    }

    private Integer queryInteger(JdbcTemplate jdbcTemplate, String sql) {
        Number result = jdbcTemplate.queryForObject(sql, Number.class);
        return result == null ? null : result.intValue();
    }

    private Map<String, String> convertMap(Map<String, Object> row) {
        Map<String, String> converted = new LinkedHashMap<>();
        row.forEach((key, value) -> converted.put(key, Objects.toString(value, null)));
        return converted;
    }

    private int toInt(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.parseInt(Objects.toString(value));
    }

    private boolean jdbcEncrypted(NodeValidationResult result) {
        return result.reachable()
                && hasText(result.sslSession().get("Ssl_version"))
                && hasText(result.sslSession().get("Ssl_cipher"));
    }

    private boolean groupSslEnabled(NodeValidationResult result) {
        return result.reachable()
                && !"DISABLED".equalsIgnoreCase(result.variables().get("group_replication_ssl_mode"));
    }

    private boolean recoverySslEnabled(NodeValidationResult result) {
        return result.reachable()
                && isEnabled(result.variables().get("group_replication_recovery_use_ssl"))
                && isRecoveryChannelSecure(result.localMember(), result.recoveryChannel());
    }

    private boolean memberOnline(NodeValidationResult result) {
        return result.reachable()
                && result.localMember() != null
                && "ONLINE".equalsIgnoreCase(result.localMember().state());
    }

    private boolean isRecoveryChannelSslAllowed(RecoveryChannelView recoveryChannel) {
        return recoveryChannel != null && "Yes".equalsIgnoreCase(recoveryChannel.sslAllowed());
    }

    private boolean isRecoveryChannelSecure(ClusterMemberView localMember, RecoveryChannelView recoveryChannel) {
        if (isPrimary(localMember) && recoveryChannel == null) {
            return true;
        }
        return isRecoveryChannelSslAllowed(recoveryChannel);
    }

    private String recoveryChannelDetail(ClusterMemberView localMember, RecoveryChannelView recoveryChannel) {
        if (recoveryChannel == null && isPrimary(localMember)) {
            return "primary_member_no_active_recovery_channel";
        }
        return recoveryChannel == null ? "n/a" : recoveryChannel.sslAllowed();
    }

    private boolean isPrimary(ClusterMemberView localMember) {
        return localMember != null && "PRIMARY".equalsIgnoreCase(localMember.role());
    }

    private boolean isEnabled(String value) {
        return "ON".equalsIgnoreCase(value) || "1".equals(value) || "YES".equalsIgnoreCase(value);
    }

    private boolean isDisabled(String value) {
        return "OFF".equalsIgnoreCase(value) || "0".equals(value) || "NO".equalsIgnoreCase(value);
    }

    private boolean hasText(String value) {
        return StringUtils.hasText(value) && !"NONE".equalsIgnoreCase(value) && !"".equals(value);
    }

    private boolean proxyValid(List<ProxyValidationResult> results, String proxyName) {
        return results.stream()
                .filter(result -> proxyName.equals(result.proxyName()))
                .findFirst()
                .map(ProxyValidationResult::valid)
                .orElse(false);
    }

    private String buildJdbcUrl(String host, int port, String database) {
        return "jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&allowPublicKeyRetrieval=true"
                .formatted(host, port, database);
    }
}
