package com.jdktomcat.showcase.data.mysql.mgr.ssl.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

@Component
public class MysqlMgrDataSourceFactory {

    private final MysqlMgrSslProperties properties;

    public MysqlMgrDataSourceFactory(MysqlMgrSslProperties properties) {
        this.properties = properties;
    }

    public DruidDataSource createDataSource(String host, int port, String database) {
        return createDataSource(host, port, database, properties.getUsername(), properties.getPassword());
    }

    public DruidDataSource createDataSource(String host, int port, String database, String username, String password) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setName(buildDataSourceName(host, port, database));
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(buildJdbcUrl(host, port, database));
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setInitialSize(1);
        dataSource.setMinIdle(1);
        dataSource.setMaxActive(4);
        dataSource.setMaxWait(5000);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestOnBorrow(false);
        dataSource.setTestWhileIdle(true);
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(300000);
        dataSource.setConnectProperties(buildConnectProperties());
        return dataSource;
    }

    private String buildJdbcUrl(String host, int port, String database) {
        return "jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&allowPublicKeyRetrieval=true"
                .formatted(host, port, database);
    }

    private String buildDataSourceName(String host, int port, String database) {
        return "mgr_%s_%d_%s".formatted(host, port, database)
                .replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }

    private Properties buildConnectProperties() {
        Properties connectProperties = new Properties();
        connectProperties.setProperty("connectTimeout", String.valueOf(properties.getConnectTimeoutMs()));
        connectProperties.setProperty("socketTimeout", String.valueOf(properties.getSocketTimeoutMs()));
        connectProperties.setProperty("sslMode", properties.getSslMode());
        connectProperties.setProperty("enabledTLSProtocols", properties.getEnabledTlsProtocols());
        connectProperties.setProperty("trustCertificateKeyStoreUrl", resolveTrustStoreUrl());
        connectProperties.setProperty("trustCertificateKeyStorePassword", properties.getTrustCertificateKeyStorePassword());
        connectProperties.setProperty("trustCertificateKeyStoreType", properties.getTrustCertificateKeyStoreType());
        return connectProperties;
    }

    private String resolveTrustStoreUrl() {
        String configuredUrl = properties.getTrustCertificateKeyStoreUrl();
        if (configuredUrl == null || !configuredUrl.startsWith("file:")) {
            return configuredUrl;
        }

        String configuredPath = configuredUrl.substring("file:".length());
        Path directPath = Paths.get(configuredPath).normalize();
        if (Files.exists(directPath)) {
            return directPath.toUri().toString();
        }

        String normalizedRelativePath = configuredPath.startsWith("./") ? configuredPath.substring(2) : configuredPath;
        Path currentDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        Path[] candidates = new Path[] {
                currentDir.resolve(normalizedRelativePath).normalize(),
                currentDir.resolve("showcase-data-mysql-mgr-ssl").resolve(normalizedRelativePath).normalize(),
                currentDir.resolveSibling("showcase-data-mysql-mgr-ssl").resolve(normalizedRelativePath).normalize()
        };

        for (Path candidate : candidates) {
            if (Files.exists(candidate)) {
                return candidate.toUri().toString();
            }
        }

        throw new IllegalStateException(
                "Truststore not found. Checked configured path '%s' from working directory '%s'. " +
                        "Run ./docker/scripts/generate-certs.sh or set SHOWCASE_MGR_TRUSTSTORE_URL explicitly."
                        .formatted(configuredUrl, currentDir)
        );
    }
}
