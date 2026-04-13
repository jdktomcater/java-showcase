package com.jdktomcat.showcase.data.mysql.mgr.ssl.model;

import java.util.Map;

public record ProxyValidationResult(
        String proxyName,
        String jdbcUrl,
        boolean reachable,
        boolean valid,
        String routedHost,
        Map<String, String> serverVariables,
        Map<String, String> sslSession,
        String errorMessage
) {
}
