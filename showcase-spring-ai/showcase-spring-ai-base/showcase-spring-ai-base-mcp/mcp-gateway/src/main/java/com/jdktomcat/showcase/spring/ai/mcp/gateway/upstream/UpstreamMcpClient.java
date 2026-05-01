package com.jdktomcat.showcase.spring.ai.mcp.gateway.upstream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jdktomcat.showcase.spring.ai.mcp.gateway.config.McpGatewayProperties;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class UpstreamMcpClient {

    private static final Logger log = LoggerFactory.getLogger(UpstreamMcpClient.class);

    private final McpGatewayProperties.Upstream properties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final Map<String, PendingRequest> pendingRequests = new ConcurrentHashMap<>();
    private final AtomicBoolean started = new AtomicBoolean(false);
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final AtomicReference<URI> messageUri = new AtomicReference<>();
    private final AtomicReference<String> lastError = new AtomicReference<>();
    private final Object initializeMonitor = new Object();
    private volatile Thread sseThread;

    public UpstreamMcpClient(McpGatewayProperties.Upstream properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public String name() {
        return properties.getName();
    }

    public void start() {
        if (!properties.isEnabled() || !started.compareAndSet(false, true)) {
            return;
        }
        sseThread = new Thread(this::runSseLoop, "mcp-upstream-sse-" + properties.getName());
        sseThread.setDaemon(true);
        sseThread.start();
    }

    public JsonNode request(JsonNode originalRequest) {
        ensureInitialized();
        return requestInternal(originalRequest, true);
    }

    private JsonNode requestInternal(JsonNode originalRequest, boolean waitForResponse) {
        start();
        URI endpoint = waitForMessageUri();
        ObjectNode upstreamRequest = originalRequest.deepCopy();
        JsonNode originalId = upstreamRequest.get("id");
        String upstreamId = null;

        CompletableFuture<JsonNode> future = null;
        if (waitForResponse) {
            upstreamId = "gw-" + UUID.randomUUID();
            upstreamRequest.put("id", upstreamId);
            future = new CompletableFuture<>();
            pendingRequests.put(upstreamId, new PendingRequest(originalId, future));
        }
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder(endpoint)
                    .timeout(properties.getRequestTimeout())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(upstreamRequest), StandardCharsets.UTF_8));
            applyHeaders(builder);
            HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new IllegalStateException("upstream " + properties.getName() + " returned HTTP " + response.statusCode()
                        + ": " + response.body());
            }
            if (!waitForResponse) {
                return null;
            }
            return future.get(properties.getRequestTimeout().toMillis(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException ex) {
            if (upstreamId != null) {
                pendingRequests.remove(upstreamId);
            }
            throw new IllegalStateException("upstream " + properties.getName() + " request timeout", ex);
        } catch (Exception ex) {
            if (upstreamId != null) {
                pendingRequests.remove(upstreamId);
            }
            throw new IllegalStateException("upstream " + properties.getName() + " request failed: " + ex.getMessage(), ex);
        }
    }

    public void restart() {
        close();
        started.set(false);
        initialized.set(false);
        start();
    }

    public UpstreamStatus status(int cachedToolCount) {
        return new UpstreamStatus(
                properties.getName(),
                properties.getBaseUrl(),
                properties.getSseEndpoint(),
                properties.isEnabled(),
                connected.get(),
                messageUri.get() != null,
                cachedToolCount,
                lastError.get()
        );
    }

    @PreDestroy
    public void close() {
        connected.set(false);
        initialized.set(false);
        messageUri.set(null);
        if (sseThread != null) {
            sseThread.interrupt();
        }
        pendingRequests.forEach((id, pending) ->
                pending.future().completeExceptionally(new IllegalStateException("upstream connection closed")));
        pendingRequests.clear();
    }

    private URI waitForMessageUri() {
        long deadline = System.nanoTime() + properties.getRequestTimeout().toNanos();
        while (System.nanoTime() < deadline) {
            URI endpoint = messageUri.get();
            if (endpoint != null) {
                return endpoint;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("interrupted while waiting upstream endpoint", ex);
            }
        }
        throw new IllegalStateException("upstream " + properties.getName() + " message endpoint is not ready");
    }

    private void ensureInitialized() {
        if (initialized.get()) {
            return;
        }
        synchronized (initializeMonitor) {
            if (initialized.get()) {
                return;
            }
            requestInternal(initializeRequest(), true);
            requestInternal(initializedNotification(), false);
            initialized.set(true);
        }
    }

    private void runSseLoop() {
        while (started.get() && !Thread.currentThread().isInterrupted()) {
            try {
                HttpRequest.Builder builder = HttpRequest.newBuilder(resolve(properties.getSseEndpoint()))
                        .header("Accept", "text/event-stream")
                        .GET();
                applyHeaders(builder);
                HttpResponse<InputStream> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofInputStream());
                if (response.statusCode() >= 400) {
                    throw new IllegalStateException("SSE HTTP " + response.statusCode());
                }
                connected.set(true);
                lastError.set(null);
                parseSse(response.body());
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            } catch (Exception ex) {
                connected.set(false);
                messageUri.set(null);
                lastError.set(ex.getMessage());
                log.warn("MCP upstream [{}] SSE disconnected: {}", properties.getName(), ex.getMessage());
                sleepBeforeReconnect();
            }
        }
    }

    private void parseSse(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String event = "message";
            StringBuilder data = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null && !Thread.currentThread().isInterrupted()) {
                if (line.isEmpty()) {
                    dispatchEvent(event, data.toString());
                    event = "message";
                    data.setLength(0);
                    continue;
                }
                if (line.startsWith("event:")) {
                    event = line.substring("event:".length()).trim();
                } else if (line.startsWith("data:")) {
                    if (!data.isEmpty()) {
                        data.append('\n');
                    }
                    data.append(line.substring("data:".length()).trim());
                }
            }
        } finally {
            connected.set(false);
        }
    }

    private void dispatchEvent(String event, String data) throws IOException {
        if (data == null || data.isBlank()) {
            return;
        }
        if ("endpoint".equals(event)) {
            messageUri.set(resolve(data));
            log.info("MCP upstream [{}] message endpoint ready: {}", properties.getName(), messageUri.get());
            return;
        }
        if (!"message".equals(event)) {
            return;
        }
        JsonNode response = objectMapper.readTree(data);
        JsonNode id = response.get("id");
        if (id == null) {
            return;
        }
        PendingRequest pending = pendingRequests.remove(id.asText());
        if (pending == null) {
            return;
        }
        ObjectNode mappedResponse = response.deepCopy();
        if (pending.originalId() != null && !pending.originalId().isNull()) {
            mappedResponse.set("id", pending.originalId());
        }
        pending.future().complete(mappedResponse);
    }

    private URI resolve(String pathOrUrl) {
        URI uri = URI.create(pathOrUrl);
        if (uri.isAbsolute()) {
            return uri;
        }
        URI baseUri = URI.create(properties.getBaseUrl());
        String path = pathOrUrl.startsWith("/") ? pathOrUrl : "/" + pathOrUrl;
        return baseUri.resolve(path);
    }

    private void applyHeaders(HttpRequest.Builder builder) {
        for (McpGatewayProperties.Header header : properties.getHeaders()) {
            if (header.getName() != null && header.getValue() != null) {
                builder.header(header.getName(), header.getValue());
            }
        }
    }

    private void sleepBeforeReconnect() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private ObjectNode initializeRequest() {
        ObjectNode request = objectMapper.createObjectNode();
        request.put("jsonrpc", "2.0");
        request.put("id", "gw-initialize");
        request.put("method", "initialize");
        ObjectNode params = request.putObject("params");
        params.put("protocolVersion", "2024-11-05");
        params.putObject("capabilities");
        ObjectNode clientInfo = params.putObject("clientInfo");
        clientInfo.put("name", "mcp-gateway");
        clientInfo.put("version", "1.0.0");
        return request;
    }

    private ObjectNode initializedNotification() {
        ObjectNode request = objectMapper.createObjectNode();
        request.put("jsonrpc", "2.0");
        request.put("method", "notifications/initialized");
        return request;
    }

    public static String appendQueryParam(String url, String name, String value) {
        if (value == null || value.isBlank()) {
            return url;
        }
        String separator = url.contains("?") ? "&" : "?";
        return url + separator + URLEncoder.encode(name, StandardCharsets.UTF_8)
                + "=" + URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private record PendingRequest(JsonNode originalId, CompletableFuture<JsonNode> future) {
    }
}
