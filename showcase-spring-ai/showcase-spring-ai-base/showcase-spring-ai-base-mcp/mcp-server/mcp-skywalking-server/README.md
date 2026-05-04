# mcp-skywalking-server

基于 Spring AI MCP Server 的 Apache SkyWalking 查询工具服务，使用 SSE 暴露 MCP Tool，内部调用 SkyWalking OAP GraphQL Query API。

## 启动

先启动 SkyWalking OAP，并确认 GraphQL API 可访问。默认地址：

```bash
http://localhost:12800/graphql
```

启动 MCP Server：

```bash
mvn -pl showcase-spring-ai-base/showcase-spring-ai-base-mcp/mcp-server/mcp-skywalking-server spring-boot:run
```

自定义 OAP 地址或认证 Header：

```bash
mvn -pl showcase-spring-ai-base/showcase-spring-ai-base-mcp/mcp-server/mcp-skywalking-server \
  spring-boot:run \
  -Dspring-boot.run.jvmArguments="-DSKYWALKING_GRAPHQL_ENDPOINT=http://127.0.0.1:12800/graphql -DSKYWALKING_AUTH_HEADER=Authorization -DSKYWALKING_AUTH_VALUE='Bearer xxx'"
```

端点：

- SSE 订阅端点：`GET http://localhost:8093/sse`
- MCP 消息端点：`POST http://localhost:8093/mcp/messages`

## Tools

| Tool | 说明 |
|---|---|
| `checkHealth` | 检查 SkyWalking OAP 健康状态 |
| `getTimeInfo` | 获取 OAP 当前时间和时区 |
| `listLayers` | 列出可用监控 Layer |
| `listServices` | 按 Layer 列出服务 |
| `findService` | 按服务名查找服务元数据 |
| `listInstances` | 查询服务实例 |
| `findEndpoints` | 查询服务 Endpoint |
| `getGlobalTopology` | 查询全局服务拓扑 |
| `queryBasicTraces` | 查询 Trace 摘要 |
| `queryTrace` | 查询完整 Trace Span 明细 |
| `listMetrics` | 列出 Metrics 定义 |
| `execMetricsExpression` | 执行 SkyWalking MQE 表达式 |
