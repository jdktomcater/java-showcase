package com.jdktomcat.showcase.spring.ai.mcp.gateway.mcp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jdktomcat.showcase.spring.ai.mcp.gateway.audit.AuditService;
import com.jdktomcat.showcase.spring.ai.mcp.gateway.config.McpGatewayProperties;
import com.jdktomcat.showcase.spring.ai.mcp.gateway.security.GatewayPrincipal;
import com.jdktomcat.showcase.spring.ai.mcp.gateway.upstream.ToolDescriptor;
import com.jdktomcat.showcase.spring.ai.mcp.gateway.upstream.UpstreamRegistry;
import org.springframework.stereotype.Service;

@Service
public class McpGatewayService {

    private final McpGatewayProperties properties;
    private final UpstreamRegistry upstreamRegistry;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    public McpGatewayService(McpGatewayProperties properties, UpstreamRegistry upstreamRegistry,
                             AuditService auditService, ObjectMapper objectMapper) {
        this.properties = properties;
        this.upstreamRegistry = upstreamRegistry;
        this.auditService = auditService;
        this.objectMapper = objectMapper;
    }

    public JsonNode handle(JsonNode request, GatewayPrincipal principal) {
        long start = System.nanoTime();
        String method = request.path("method").asText(null);
        String toolName = request.path("params").path("name").asText(null);
        String upstream = null;
        boolean allowed = true;
        String error = null;
        try {
            JsonNode response = switch (method) {
                case "initialize" -> initializeResponse(request);
                case "ping" -> resultResponse(request, objectMapper.createObjectNode());
                case "tools/list" -> listToolsResponse(request, principal);
                case "tools/call" -> callTool(request, principal);
                case "resources/list", "prompts/list" -> emptyListResponse(request, method);
                case "notifications/initialized", "notifications/cancelled" -> null;
                default -> errorResponse(request, -32601, "method not found: " + method);
            };
            if ("tools/call".equals(method) && toolName != null) {
                upstream = namespaceOf(toolName);
            }
            return response;
        } catch (Exception ex) {
            error = ex.getMessage();
            allowed = !(ex instanceof ForbiddenToolException);
            return errorResponse(request, allowed ? -32000 : -32001, ex.getMessage());
        } finally {
            auditService.record(principal.name(), method, toolName, upstream, allowed,
                    System.nanoTime() - start, error);
        }
    }

    private JsonNode initializeResponse(JsonNode request) {
        ObjectNode result = objectMapper.createObjectNode();
        result.put("protocolVersion", request.path("params").path("protocolVersion").asText("2024-11-05"));
        ObjectNode capabilities = result.putObject("capabilities");
        capabilities.putObject("tools").put("listChanged", true);
        ObjectNode serverInfo = result.putObject("serverInfo");
        serverInfo.put("name", properties.getName());
        serverInfo.put("version", properties.getVersion());
        result.put("instructions", "MCP Gateway: route tool calls by namespace prefix, for example mysql.listTables.");
        return resultResponse(request, result);
    }

    private JsonNode listToolsResponse(JsonNode request, GatewayPrincipal principal) {
        ObjectNode result = objectMapper.createObjectNode();
        ArrayNode tools = result.putArray("tools");
        for (ToolDescriptor descriptor : upstreamRegistry.tools()) {
            if (isToolAllowed(principal, descriptor.gatewayToolName())) {
                tools.add(descriptor.tool());
            }
        }
        return resultResponse(request, result);
    }

    private JsonNode callTool(JsonNode request, GatewayPrincipal principal) {
        String gatewayToolName = request.path("params").path("name").asText(null);
        if (gatewayToolName == null || gatewayToolName.isBlank()) {
            throw new IllegalArgumentException("tools/call params.name is required");
        }
        if (!isToolAllowed(principal, gatewayToolName)) {
            throw new ForbiddenToolException("tool is not allowed for api key: " + gatewayToolName);
        }
        ToolDescriptor descriptor = upstreamRegistry.findTool(gatewayToolName)
                .orElseThrow(() -> new IllegalArgumentException("unknown tool: " + gatewayToolName));
        ObjectNode upstreamRequest = request.deepCopy();
        JsonNode paramsNode = upstreamRequest.get("params");
        ObjectNode params;
        if (paramsNode instanceof ObjectNode objectNode) {
            params = objectNode;
        } else {
            params = objectMapper.createObjectNode();
            upstreamRequest.set("params", params);
        }
        params.put("name", descriptor.upstreamToolName());
        return upstreamRegistry.call(descriptor.upstream(), upstreamRequest);
    }

    private JsonNode emptyListResponse(JsonNode request, String method) {
        ObjectNode result = objectMapper.createObjectNode();
        if ("resources/list".equals(method)) {
            result.putArray("resources");
        } else {
            result.putArray("prompts");
        }
        return resultResponse(request, result);
    }

    private JsonNode resultResponse(JsonNode request, JsonNode result) {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        copyId(request, response);
        response.set("result", result);
        return response;
    }

    private JsonNode errorResponse(JsonNode request, int code, String message) {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        copyId(request, response);
        ObjectNode error = response.putObject("error");
        error.put("code", code);
        error.put("message", message == null ? "gateway error" : message);
        return response;
    }

    private void copyId(JsonNode request, ObjectNode response) {
        JsonNode id = request.get("id");
        if (id == null || id.isNull()) {
            response.putNull("id");
        } else {
            response.set("id", id);
        }
    }

    private boolean isToolAllowed(GatewayPrincipal principal, String toolName) {
        if (principal.allowedTools() == null || principal.allowedTools().isEmpty()) {
            return false;
        }
        for (String pattern : principal.allowedTools()) {
            if ("*".equals(pattern) || toolName.equals(pattern)) {
                return true;
            }
            if (pattern.endsWith("*") && toolName.startsWith(pattern.substring(0, pattern.length() - 1))) {
                return true;
            }
        }
        return false;
    }

    private String namespaceOf(String toolName) {
        int dot = toolName.indexOf('.');
        return dot > 0 ? toolName.substring(0, dot) : "unknown";
    }

    private static final class ForbiddenToolException extends RuntimeException {
        private ForbiddenToolException(String message) {
            super(message);
        }
    }
}
