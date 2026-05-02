# mcp-gateway

轻量级 MCP over SSE 网关示例。对外暴露一个 MCP Server SSE 入口，对内聚合多个 MCP SSE 上游，并用工具名前缀完成命名空间路由。

默认配置已接好本仓库的 `mcp-mysql-server`：

- 上游：`http://localhost:8090/sse`
- 网关：`http://localhost:8095/sse`
- API Key：`demo-gateway-key`
- 工具命名空间：`mysql.*`

## 功能

| 能力 | 实现 |
|---|---|
| SSE 上游聚合 | 网关为每个上游维护 SSE 会话，调用前自动完成 MCP initialize 握手 |
| 命名空间路由 | 上游工具在网关侧改名为 `{upstream}.{tool}`，例如 `mysql.listTables` |
| API Key 鉴权 | 支持 `X-API-Key` Header 或 `api_key` query 参数 |
| 工具白名单 | 每个 API Key 可配置 `allowed-tools`，支持精确匹配和 `mysql.*` 形式 |
| 令牌桶限流 | 每个 API Key 独立配置 `capacity` 与 `refill-per-minute` |
| 审计日志 | 通过 `MCP_AUDIT` logger 输出 principal、method、tool、upstream、耗时和结果 |
| Actuator 指标 | 暴露 `mcp.gateway.requests` 与 `mcp.gateway.request.duration` |
| 运维端点 | `/admin/upstreams`、`/admin/tools`、`/admin/refresh` |

## 启动

先启动 MySQL MCP Server：

```bash
mvn -pl showcase-spring-ai-base/showcase-spring-ai-base-mcp/mcp-server/mcp-mysql-server spring-boot:run
```

再启动网关：

```bash
mvn -pl showcase-spring-ai-base/showcase-spring-ai-base-mcp/mcp-gateway spring-boot:run
```

## 运维端点

```bash
curl -H 'X-API-Key: demo-gateway-key' http://localhost:8095/admin/upstreams

curl -H 'X-API-Key: demo-gateway-key' http://localhost:8095/admin/tools

curl -X POST -H 'X-API-Key: demo-gateway-key' http://localhost:8095/admin/refresh
```

## MCP 调用验证

用 query 参数方式最容易用 `curl` 验证，因为 SSE 端点返回的 message endpoint 会保留 `api_key`：

```bash
curl -N 'http://localhost:8095/sse?api_key=demo-gateway-key'
```

拿到返回里的 `sessionId` 后，按 MCP JSON-RPC 调用：

```bash
SESSION_ID=替换为实际sessionId

curl -X POST "http://localhost:8095/mcp/messages?sessionId=${SESSION_ID}&api_key=demo-gateway-key" \
  -H 'Content-Type: application/json' \
  -d '{"jsonrpc":"2.0","id":1,"method":"initialize","params":{"protocolVersion":"2024-11-05","capabilities":{},"clientInfo":{"name":"curl","version":"1"}}}'

curl -X POST "http://localhost:8095/mcp/messages?sessionId=${SESSION_ID}&api_key=demo-gateway-key" \
  -H 'Content-Type: application/json' \
  -d '{"jsonrpc":"2.0","method":"notifications/initialized"}'

curl -X POST "http://localhost:8095/mcp/messages?sessionId=${SESSION_ID}&api_key=demo-gateway-key" \
  -H 'Content-Type: application/json' \
  -d '{"jsonrpc":"2.0","id":2,"method":"tools/list","params":{}}'

curl -X POST "http://localhost:8095/mcp/messages?sessionId=${SESSION_ID}&api_key=demo-gateway-key" \
  -H 'Content-Type: application/json' \
  -d '{"jsonrpc":"2.0","id":3,"method":"tools/call","params":{"name":"mysql.listTables","arguments":{}}}'
```

最后一个调用会通过网关路由到 `mcp-mysql-server` 的 `listTables`，SSE 流中可看到类似：

```json
{"jsonrpc":"2.0","id":3,"result":{"content":[{"type":"text","text":"[\"t_order\",\"t_user\"]"}],"isError":false}}
```

## 配置

核心配置在 `src/main/resources/application.yml`：

```yaml
mcp:
  gateway:
    auth:
      clients:
        - name: demo
          api-key: demo-gateway-key
          allowed-tools:
            - mysql.*
          rate-limit:
            capacity: 120
            refill-per-minute: 120
    upstreams:
      - name: mysql
        enabled: true
        base-url: http://localhost:8090
        sse-endpoint: /sse
        request-timeout: 30s
```

新增上游时增加 `upstreams` 项即可；网关会用上游 `name` 作为工具命名空间。
