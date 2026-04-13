package com.jdktomcat.showcase.data.mysql.mgr.ssl.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.stereotype.Component;

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
        dataSource.setName("%s:%d/%s".formatted(host, port, database));
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

    private Properties buildConnectProperties() {
        Properties connectProperties = new Properties();
        connectProperties.setProperty("connectTimeout", String.valueOf(properties.getConnectTimeoutMs()));
        connectProperties.setProperty("socketTimeout", String.valueOf(properties.getSocketTimeoutMs()));
        connectProperties.setProperty("sslMode", properties.getSslMode());
        connectProperties.setProperty("enabledTLSProtocols", properties.getEnabledTlsProtocols());
        connectProperties.setProperty("trustCertificateKeyStoreUrl", properties.getTrustCertificateKeyStoreUrl());
        connectProperties.setProperty("trustCertificateKeyStorePassword", properties.getTrustCertificateKeyStorePassword());
        connectProperties.setProperty("trustCertificateKeyStoreType", properties.getTrustCertificateKeyStoreType());
        return connectProperties;
    }
}
