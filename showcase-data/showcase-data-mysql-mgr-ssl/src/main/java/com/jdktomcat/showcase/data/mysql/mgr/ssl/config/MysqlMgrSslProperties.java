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
    private Proxy proxy = new Proxy();

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

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
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

    public static class Proxy {
        private boolean enabled = true;
        private Endpoint readWrite = new Endpoint("f5-rw", "127.0.0.1", 13306, "mgr_demo");
        private Endpoint readOnly = new Endpoint("f5-ro", "127.0.0.1", 13307, "mgr_demo");

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public Endpoint getReadWrite() {
            return readWrite;
        }

        public void setReadWrite(Endpoint readWrite) {
            this.readWrite = readWrite;
        }

        public Endpoint getReadOnly() {
            return readOnly;
        }

        public void setReadOnly(Endpoint readOnly) {
            this.readOnly = readOnly;
        }
    }

    public static class Endpoint {
        private String name;
        private String host;
        private int port;
        private String database;

        public Endpoint() {
        }

        public Endpoint(String name, String host, int port, String database) {
            this.name = name;
            this.host = host;
            this.port = port;
            this.database = database;
        }

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
