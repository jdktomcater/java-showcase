# showcase-spring-ai-mcp

基于 [Spring AI 1.0.3](https://docs.spring.io/spring-ai/reference/) 的 **MCP（Model Context Protocol）** 基础调用示例。

通过一个最小可运行的「MySQL 只读查询工具」展示 MCP 的两端：

| 子模块 | 角色 | 说明 |
|---|---|---|
| `mcp-mysql-server` | **MCP Server** | 暴露 `listTables`、`describeTable`、`executeSelect` 三个 Tool，使用 SSE 传输（基于 Spring MVC） |
| `mcp-skywalking-server` | **MCP Server** | 对接 Apache SkyWalking OAP GraphQL Query API，暴露健康检查、服务、拓扑、Trace、Metrics 查询 Tool |
| `mcp-mysql-client` | **MCP Client** | 通过 SSE 连接到 Server，启动时自动列出工具并依次调用，将结果打印到控制台 |
| `mcp-mysql-llm` | **LLM + MCP** | Ollama 大模型 + Spring AI ChatClient，把 MCP Tool 作为 Function 暴露给模型，由 LLM 自主调用工具完成自然语言查库 |
| `mcp-gateway` | **MCP Gateway** | 聚合多个 SSE MCP 上游，提供命名空间路由、API Key 鉴权、工具白名单、限流、审计、指标和运维端点 |

> 数据源默认使用 H2（开启 MySQL 兼容模式），免安装即可体验；如需连真实 MySQL，按 [切换到真实 MySQL](#切换到真实-mysql) 一节调整即可。

## 运行步骤

### 1. 构建

```bash
mvn -pl showcase-spring-ai-base/showcase-spring-ai-base-mcp -am clean package -DskipTests
```

### 2. 启动 MCP Server（端口 8090）

```bash
mvn -pl showcase-spring-ai-base/showcase-spring-ai-base-mcp/mcp-server/mcp-mysql-server spring-boot:run
```

启动成功后，可见如下端点：

- SSE 订阅端点：`GET  http://localhost:8090/sse`
- MCP 消息端点：`POST http://localhost:8090/mcp/messages`

### 3. （可选）启动 SkyWalking MCP Server（端口 8093）

前置条件：已有 SkyWalking OAP，且 GraphQL Query API 可访问。默认连接：

```bash
http://localhost:12800/graphql
```

启动：

```bash
mvn -pl showcase-spring-ai-base/showcase-spring-ai-base-mcp/mcp-server/mcp-skywalking-server spring-boot:run
```

如需指定 OAP 地址或认证 Header：

```bash
mvn -pl showcase-spring-ai-base/showcase-spring-ai-base-mcp/mcp-server/mcp-skywalking-server \
  spring-boot:run \
  -Dspring-boot.run.jvmArguments="-DSKYWALKING_GRAPHQL_ENDPOINT=http://127.0.0.1:12800/graphql -DSKYWALKING_AUTH_HEADER=Authorization -DSKYWALKING_AUTH_VALUE='Bearer xxx'"
```

启动成功后，可见如下端点：

- SSE 订阅端点：`GET  http://localhost:8093/sse`
- MCP 消息端点：`POST http://localhost:8093/mcp/messages`

### 4. 启动 MCP Client（另开一个终端）

```bash
mvn -pl showcase-spring-ai-base/showcase-spring-ai-base-mcp/mcp-client/mcp-mysql-client spring-boot:run
```

控制台将依次输出：

1. 已发现的 MCP Tool 列表（含名称与描述）；
2. `listTables` → 当前库中的表名；
3. `describeTable(t_user)` → t_user 表结构；
4. `executeSelect(...)` → 用户与订单的关联查询结果。

### 5. （可选）启动 MCP Gateway（端口 8095）

先保持 `mcp-mysql-server` 运行，再启动网关：

```bash
mvn -pl showcase-spring-ai-base/showcase-spring-ai-base-mcp/mcp-gateway spring-boot:run
```

默认 API Key 为 `demo-gateway-key`，默认聚合 `mcp-mysql-server`，并把工具暴露为：

- `mysql.listTables`
- `mysql.describeTable`
- `mysql.executeSelect`

如需同时聚合 `mcp-skywalking-server`，先启动 SkyWalking MCP Server，再启用网关上游：

```bash
MCP_SKYWALKING_UPSTREAM_ENABLED=true \
mvn -pl showcase-spring-ai-base/showcase-spring-ai-base-mcp/mcp-gateway spring-boot:run
```

SkyWalking 工具会以 `skywalking.*` 命名空间暴露，例如：

- `skywalking.checkHealth`
- `skywalking.listServices`
- `skywalking.queryBasicTraces`
- `skywalking.execMetricsExpression`

运维端点：

```bash
curl -H 'X-API-Key: demo-gateway-key' http://localhost:8095/admin/upstreams
curl -H 'X-API-Key: demo-gateway-key' http://localhost:8095/admin/tools
curl -X POST -H 'X-API-Key: demo-gateway-key' http://localhost:8095/admin/refresh
```

详见 `mcp-gateway/README.md`。

### 6. （可选）启动 LLM 示例：通过 MCP Gateway 让大模型查库（端口 8092）

前置条件：本机已安装 [Ollama](https://ollama.com)，并 pull 一个**支持 tools / function-calling** 的模型，例如：

```bash
ollama pull qwen2.5:7b      # 或 llama3.1:8b、qwen3:8b 等
ollama serve                # 默认监听 11434
```

启动 LLM 应用：

```bash
mvn -pl showcase-spring-ai-base/showcase-spring-ai-base-mcp/mcp-client/mcp-mysql-llm spring-boot:run
```

`mcp-mysql-llm` 默认连接 `mcp-gateway` 的 `http://localhost:8095/sse?api_key=demo-gateway-key`，
由网关再路由到 `mcp-mysql-server`。然后用自然语言提问，由模型自己决定何时调用
`mysql.listTables` / `mysql.describeTable` / `mysql.executeSelect`：

```bash
# GET 形式（注意 URL 编码）
curl 'http://localhost:8092/ai/ask?question=数据库里都有哪些表？'

# POST 形式
curl -X POST 'http://localhost:8092/ai/ask' \
     -H 'Content-Type: text/plain' \
     --data '统计每个城市的用户数，按用户数倒序'

curl -X POST 'http://localhost:8092/ai/ask' \
     -H 'Content-Type: text/plain' \
     --data '订单金额最高的 3 笔订单分别是哪个用户下的？'
```

> 默认模型为 `qwen2.5:7b`，可在 `application.yml` 的 `spring.ai.ollama.chat.options.model` 修改。

数据流：

```
HTTP /ai/ask
   │
   ▼
ChatClient（Ollama 推理）
   │   ⇆  Tool Calling
   ▼
SyncMcpToolCallbackProvider（自动装配）
   │
   ▼  SSE
mcp-gateway  →  mcp-mysql-server  →  JdbcTemplate  →  MySQL/H2
```

## 工具一览

| Tool | 说明 | 关键参数 |
|---|---|---|
| `listTables` | 列出当前数据库所有表 | 无 |
| `describeTable` | 查看指定表结构 | `tableName` |
| `executeSelect` | 执行只读 SQL（仅 SELECT/SHOW/DESC） | `sql`、`maxRows`（可选，默认 100，最大 500） |
| `checkHealth` | 检查 SkyWalking OAP 健康状态 | 无 |
| `listLayers` | 列出 SkyWalking 当前可用 Layer | 无 |
| `listServices` | 按 Layer 列出服务 | `layer`（可选） |
| `findService` | 按服务名查找服务元数据 | `serviceName` |
| `listInstances` | 查询服务实例 | `serviceId`、时间范围（可选） |
| `findEndpoints` | 查询服务 Endpoint | `serviceId`、`keyword`（可选） |
| `getGlobalTopology` | 查询全局服务拓扑 | `layer`、时间范围（可选） |
| `queryBasicTraces` | 查询 Trace 摘要 | 服务、实例、Endpoint、Trace ID、耗时、分页、时间范围（均可选） |
| `queryTrace` | 按 Trace ID 查询完整 Span 明细 | `traceId` |
| `listMetrics` | 列出 SkyWalking Metrics 定义 | `regex`（可选） |
| `execMetricsExpression` | 执行 SkyWalking MQE 表达式 | `expression`、实体信息、时间范围（可选） |

经 `mcp-gateway` 暴露时，工具名会加上命名空间前缀：`mysql.listTables`、`skywalking.checkHealth` 等。

> 安全约束：`executeSelect` 拒绝非只读语句（INSERT/UPDATE/DELETE/DDL 等），并对返回行数做硬上限。

## 接入第三方 MCP 客户端

`mcp-mysql-server` 是标准 MCP over SSE 实现，可被任何兼容 MCP 协议的客户端接入，例如 **Cursor**、**Claude Desktop**、**Cherry Studio** 等。

以 Cursor `mcp.json` 为例（HTTP/SSE 传输）：

```json
{
  "mcpServers": {
    "mcp-mysql-server": {
      "url": "http://localhost:8090/sse"
    }
  }
}
```

## 切换到真实 MySQL

服务端已内置 `mysql` profile，只需准备好 MySQL 实例与库表，再以下方式启动：

```bash
mvn -pl showcase-spring-ai-base/showcase-spring-ai-base-mcp/mcp-server/mcp-mysql-server \
    spring-boot:run \
    -Dspring-boot.run.profiles=mysql \
    -Dspring-boot.run.jvmArguments="-Dspring.datasource.url=jdbc:mysql://127.0.0.1:3306/your_db -Dspring.datasource.username=root -Dspring.datasource.password=xxx"
```

## 模块结构

```
showcase-spring-ai-mcp
├── pom.xml                       # 聚合 + spring-ai-bom
├── mcp-server
│   └── mcp-mysql-server          # MCP Server（SSE）
│   └── mcp-skywalking-server     # SkyWalking MCP Server（SSE + GraphQL Query API）
├── mcp-gateway                   # MCP Gateway（SSE 聚合 + 鉴权 + 路由）
└── mcp-client
    ├── mcp-mysql-client          # MCP Client（SSE）
    └── mcp-mysql-llm             # LLM + MCP Gateway（Ollama + ChatClient）
```
