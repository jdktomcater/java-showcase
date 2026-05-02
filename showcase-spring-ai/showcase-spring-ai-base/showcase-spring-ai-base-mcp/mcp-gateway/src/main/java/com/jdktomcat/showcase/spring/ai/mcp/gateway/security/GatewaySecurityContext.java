package com.jdktomcat.showcase.spring.ai.mcp.gateway.security;

import jakarta.servlet.http.HttpServletRequest;

public final class GatewaySecurityContext {

    public static final String PRINCIPAL_ATTRIBUTE = GatewaySecurityContext.class.getName() + ".principal";

    private GatewaySecurityContext() {
    }

    public static GatewayPrincipal requirePrincipal(HttpServletRequest request) {
        Object principal = request.getAttribute(PRINCIPAL_ATTRIBUTE);
        if (principal instanceof GatewayPrincipal gatewayPrincipal) {
            return gatewayPrincipal;
        }
        throw new IllegalStateException("MCP Gateway principal is missing");
    }
}
