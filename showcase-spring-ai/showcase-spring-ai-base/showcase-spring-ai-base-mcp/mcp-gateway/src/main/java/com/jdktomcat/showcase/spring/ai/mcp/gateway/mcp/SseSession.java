package com.jdktomcat.showcase.spring.ai.mcp.gateway.mcp;

import com.jdktomcat.showcase.spring.ai.mcp.gateway.security.GatewayPrincipal;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public record SseSession(String id, GatewayPrincipal principal, SseEmitter emitter) {
}
