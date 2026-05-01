package com.jdktomcat.showcase.spring.ai.mcp.gateway.mcp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jdktomcat.showcase.spring.ai.mcp.gateway.config.McpGatewayProperties;
import com.jdktomcat.showcase.spring.ai.mcp.gateway.security.GatewayPrincipal;
import com.jdktomcat.showcase.spring.ai.mcp.gateway.security.GatewaySecurityContext;
import com.jdktomcat.showcase.spring.ai.mcp.gateway.upstream.UpstreamMcpClient;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class McpGatewayController {

    private final McpGatewayProperties properties;
    private final McpGatewayService gatewayService;
    private final ObjectMapper objectMapper;
    private final Map<String, SseSession> sessions = new ConcurrentHashMap<>();

    public McpGatewayController(McpGatewayProperties properties, McpGatewayService gatewayService,
                                ObjectMapper objectMapper) {
        this.properties = properties;
        this.gatewayService = gatewayService;
        this.objectMapper = objectMapper;
    }

    @GetMapping(path = "/sse", produces = "text/event-stream")
    public SseEmitter sse(HttpServletRequest request) throws IOException {
        GatewayPrincipal principal = GatewaySecurityContext.requirePrincipal(request);
        String sessionId = UUID.randomUUID().toString();
        SseEmitter emitter = new SseEmitter(0L);
        SseSession session = new SseSession(sessionId, principal, emitter);
        sessions.put(sessionId, session);
        emitter.onCompletion(() -> sessions.remove(sessionId));
        emitter.onTimeout(() -> sessions.remove(sessionId));
        emitter.onError(ignored -> sessions.remove(sessionId));

        String endpoint = properties.getMessageEndpoint() + "?sessionId=" + sessionId;
        String apiKeyFromQuery = request.getParameter(properties.getAuth().getApiKeyQueryParam());
        endpoint = UpstreamMcpClient.appendQueryParam(endpoint, properties.getAuth().getApiKeyQueryParam(), apiKeyFromQuery);
        emitter.send(SseEmitter.event().name("endpoint").data(endpoint));
        return emitter;
    }

    @PostMapping(path = "/mcp/messages")
    public ResponseEntity<Void> message(@RequestParam("sessionId") String sessionId,
                                        @RequestBody JsonNode request,
                                        HttpServletRequest httpRequest) throws IOException {
        GatewayPrincipal principal = GatewaySecurityContext.requirePrincipal(httpRequest);
        SseSession session = sessions.get(sessionId);
        if (session == null) {
            return ResponseEntity.notFound().build();
        }
        if (!session.principal().apiKey().equals(principal.apiKey())) {
            return ResponseEntity.status(403).build();
        }

        JsonNode response = gatewayService.handle(request, principal);
        if (response != null) {
            session.emitter().send(SseEmitter.event()
                    .name("message")
                    .data(objectMapper.writeValueAsString(response)));
        }
        return ResponseEntity.accepted().build();
    }
}
