package com.jdktomcat.showcase.data.mysql.mgr.ssl.service;

import com.jdktomcat.showcase.data.mysql.mgr.ssl.config.MysqlMgrSslProperties;
import com.jdktomcat.showcase.data.mysql.mgr.ssl.model.CheckResult;
import com.jdktomcat.showcase.data.mysql.mgr.ssl.model.ClusterMemberView;
import com.jdktomcat.showcase.data.mysql.mgr.ssl.model.ClusterValidationResponse;
import com.jdktomcat.showcase.data.mysql.mgr.ssl.model.NodeValidationResult;
import com.jdktomcat.showcase.data.mysql.mgr.ssl.model.RecoveryChannelView;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

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

    private final MysqlMgrSslProperties properties;

    public MysqlMgrSslValidationService(MysqlMgrSslProperties properties) {
        this.properties = properties;
    }

    public ClusterValidationResponse validateCluster() {
        List<NodeValidationResult> nodeResults = new ArrayList<>();
        for (MysqlMgrSslProperties.Node node : properties.getNodes()) {
            nodeResults.add(validateNode(node));
        }

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
                        "every node should see " + expectedNodeCount + " members in replication_group_members")
        );

        Map<String, String> hints = new LinkedHashMap<>();
        hints.put("manual_sql_1", "SHOW STATUS LIKE 'Ssl_%';");
        hints.put("manual_sql_2", "SHOW VARIABLES LIKE 'group_replication%ssl%';");
        hints.put("manual_sql_3", "SELECT * FROM performance_schema.replication_group_members;");

        boolean valid = clusterChecks.stream().allMatch(CheckResult::passed);
        return new ClusterValidationResponse(
                OffsetDateTime.now(),
                valid,
                expectedNodeCount,
                reachableNodeCount,
                clusterChecks,
                nodeResults,
                hints
        );
    }

    private NodeValidationResult validateNode(MysqlMgrSslProperties.Node node) {
        String jdbcUrl = buildJdbcUrl(node);
        try (Connection connection = openConnection(jdbcUrl)) {
            Map<String, String> variables = querySingleRow(connection, NODE_OVERVIEW_SQL);
            Map<String, String> sslSession = queryKeyValueRows(connection, SSL_STATUS_SQL, "Variable_name", "Value");
            ClusterMemberView localMember = queryOptionalMember(connection, LOCAL_MEMBER_SQL);
            List<ClusterMemberView> clusterMembers = queryMembers(connection, CLUSTER_MEMBERS_SQL);
            RecoveryChannelView recoveryChannel = queryRecoveryChannel(connection);
            Integer probeTableRows = queryInteger(connection, "SELECT COUNT(*) FROM " + node.getDatabase() + ".ssl_probe");
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
                    jdbcUrl,
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
        } catch (SQLException ex) {
            List<CheckResult> checks = List.of(
                    new CheckResult("connectivity", false, ex.getMessage())
            );
            return new NodeValidationResult(
                    node.getName(),
                    jdbcUrl,
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

    private Connection openConnection(String jdbcUrl) throws SQLException {
        Properties jdbcProperties = new Properties();
        jdbcProperties.setProperty("user", properties.getUsername());
        jdbcProperties.setProperty("password", properties.getPassword());
        jdbcProperties.setProperty("connectTimeout", String.valueOf(properties.getConnectTimeoutMs()));
        jdbcProperties.setProperty("socketTimeout", String.valueOf(properties.getSocketTimeoutMs()));
        jdbcProperties.setProperty("sslMode", properties.getSslMode());
        jdbcProperties.setProperty("enabledTLSProtocols", properties.getEnabledTlsProtocols());
        jdbcProperties.setProperty("trustCertificateKeyStoreUrl", properties.getTrustCertificateKeyStoreUrl());
        jdbcProperties.setProperty("trustCertificateKeyStorePassword", properties.getTrustCertificateKeyStorePassword());
        jdbcProperties.setProperty("trustCertificateKeyStoreType", properties.getTrustCertificateKeyStoreType());
        return DriverManager.getConnection(jdbcUrl, jdbcProperties);
    }

    private String buildJdbcUrl(MysqlMgrSslProperties.Node node) {
        return "jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&allowPublicKeyRetrieval=true"
                .formatted(node.getHost(), node.getPort(), node.getDatabase());
    }

    private Map<String, String> querySingleRow(Connection connection, String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (!resultSet.next()) {
                return Map.of();
            }
            Map<String, String> result = new LinkedHashMap<>();
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int index = 1; index <= metaData.getColumnCount(); index++) {
                result.put(metaData.getColumnLabel(index), Objects.toString(resultSet.getObject(index), null));
            }
            return result;
        }
    }

    private Map<String, String> queryKeyValueRows(Connection connection, String sql, String keyColumn, String valueColumn)
            throws SQLException {
        Map<String, String> result = new LinkedHashMap<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                result.put(resultSet.getString(keyColumn), resultSet.getString(valueColumn));
            }
        }
        return result;
    }

    private ClusterMemberView queryOptionalMember(Connection connection, String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (!resultSet.next()) {
                return null;
            }
            return new ClusterMemberView(
                    resultSet.getString("member_host"),
                    resultSet.getInt("member_port"),
                    resultSet.getString("member_state"),
                    resultSet.getString("member_role")
            );
        }
    }

    private List<ClusterMemberView> queryMembers(Connection connection, String sql) throws SQLException {
        List<ClusterMemberView> members = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                members.add(new ClusterMemberView(
                        resultSet.getString("member_host"),
                        resultSet.getInt("member_port"),
                        resultSet.getString("member_state"),
                        resultSet.getString("member_role")
                ));
            }
        }
        return members;
    }

    private RecoveryChannelView queryRecoveryChannel(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(RECOVERY_CHANNEL_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            if (!resultSet.next()) {
                return null;
            }
            return new RecoveryChannelView(
                    resultSet.getString("CHANNEL_NAME"),
                    resultSet.getString("HOST"),
                    resultSet.getObject("PORT") == null ? null : resultSet.getInt("PORT"),
                    resultSet.getString("replication_user"),
                    resultSet.getString("SSL_ALLOWED"),
                    resultSet.getString("TLS_VERSION"),
                    resultSet.getString("SSL_CA_FILE"),
                    resultSet.getString("SSL_VERIFY_SERVER_CERTIFICATE")
            );
        }
    }

    private Integer queryInteger(Connection connection, String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (!resultSet.next()) {
                return null;
            }
            return resultSet.getInt(1);
        }
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

    private boolean hasText(String value) {
        return StringUtils.hasText(value) && !"NONE".equalsIgnoreCase(value) && !"".equals(value);
    }
}
