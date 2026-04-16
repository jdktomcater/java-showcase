package com.jdktomcat.showcase.data.mysql.mgr.ssl.model;

import java.util.Map;

public class ProxyValidationResult {

    private final String proxyName;
    private final String jdbcUrl;
    private final boolean reachable;
    private final boolean valid;
    private final String routedHost;
    private final Map<String, String> serverVariables;
    private final Map<String, String> sslSession;
    private final String errorMessage;

    public ProxyValidationResult(
            String proxyName,
            String jdbcUrl,
            boolean reachable,
            boolean valid,
            String routedHost,
            Map<String, String> serverVariables,
            Map<String, String> sslSession,
            String errorMessage
    ) {
        this.proxyName = proxyName;
        this.jdbcUrl = jdbcUrl;
        this.reachable = reachable;
        this.valid = valid;
        this.routedHost = routedHost;
        this.serverVariables = serverVariables;
        this.sslSession = sslSession;
        this.errorMessage = errorMessage;
    }

    public String getProxyName() {
        return proxyName;
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

    public String getRoutedHost() {
        return routedHost;
    }

    public Map<String, String> getServerVariables() {
        return serverVariables;
    }

    public Map<String, String> getSslSession() {
        return sslSession;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
