package com.jdktomcat.showcase.data.mysql.mgr.ssl.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "showcase.mysql.mgr")
public class MysqlMgrSslProperties {

    private String username = "app_user";
    private String password = "App#123456";
    private int connectTimeoutMs = 5000;
    private int socketTimeoutMs = 5000;
    private String sslMode = "VERIFY_CA";
    private String enabledTlsProtocols = "TLSv1.2,TLSv1.3";
    private String trustCertificateKeyStoreUrl = "file:./docker/certs/client/truststore.jks";
    private String trustCertificateKeyStorePassword = "changeit";
    private String trustCertificateKeyStoreType = "JKS";
    private List<Node> nodes = new ArrayList<>();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    public void setConnectTimeoutMs(int connectTimeoutMs) {
        this.connectTimeoutMs = connectTimeoutMs;
    }

    public int getSocketTimeoutMs() {
        return socketTimeoutMs;
    }

    public void setSocketTimeoutMs(int socketTimeoutMs) {
        this.socketTimeoutMs = socketTimeoutMs;
    }

    public String getSslMode() {
        return sslMode;
    }

    public void setSslMode(String sslMode) {
        this.sslMode = sslMode;
    }

    public String getEnabledTlsProtocols() {
        return enabledTlsProtocols;
    }

    public void setEnabledTlsProtocols(String enabledTlsProtocols) {
        this.enabledTlsProtocols = enabledTlsProtocols;
    }

    public String getTrustCertificateKeyStoreUrl() {
        return trustCertificateKeyStoreUrl;
    }

    public void setTrustCertificateKeyStoreUrl(String trustCertificateKeyStoreUrl) {
        this.trustCertificateKeyStoreUrl = trustCertificateKeyStoreUrl;
    }

    public String getTrustCertificateKeyStorePassword() {
        return trustCertificateKeyStorePassword;
    }

    public void setTrustCertificateKeyStorePassword(String trustCertificateKeyStorePassword) {
        this.trustCertificateKeyStorePassword = trustCertificateKeyStorePassword;
    }

    public String getTrustCertificateKeyStoreType() {
        return trustCertificateKeyStoreType;
    }

    public void setTrustCertificateKeyStoreType(String trustCertificateKeyStoreType) {
        this.trustCertificateKeyStoreType = trustCertificateKeyStoreType;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public static class Node {
        private String name;
        private String host = "127.0.0.1";
        private int port;
        private String database = "mgr_demo";

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getDatabase() {
            return database;
        }

        public void setDatabase(String database) {
            this.database = database;
        }
    }
}
