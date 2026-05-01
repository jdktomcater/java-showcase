# showcase-spring-ai-mcp

基于 [Spring AI 1.0.3](https://docs.spring.io/spring-ai/reference/) 的 **MCP（Model Context Protocol）** 基础调用示例。

通过一个最小可运行的「MySQL 只读查询工具」展示 MCP 的两端：

| 子模块 | 角色 | 说明 |
|---|---|---|
| `mcp-mysql-server` | **MCP Server** | 暴露 `listTables`、`describeTable`、`executeSelect` 三个 Tool，使用 SSE 传输（基于 Spring MVC） |
| `mcp-mysql-client` | **MCP Client** | 通过 SSE 连接到 Server，启动时自动列出工具并依次调用，将结果打印到控制台 |
| `mcp-mysql-llm` | **LLM + MCP** | Ollama 大模型 + Spring AI ChatClient，把 MCP Tool 作为 Function 暴露给模型，由 LLM 自主调用工具完成自然语言查库 |
| `mcp-gateway` | **MCP Gateway** | 聚合多个 SSE MCP 上游，提供命名空间路由、API Key 鉴权、工具白名单、限流、审计、指标和运维端点 |

> 数据源默认使用 H2（开启 MySQL 兼容模式），免安装即可体验；如需连真实 MySQL，按 [切换到真实 MySQL](#切换到真实-mysql) 一节调整即可。

## 运行步骤

### 1. 构建

```bash
mvn -pl showcase-spring-ai/showcase-spring-ai-mcp -am clean package -DskipTests
```

### 2. 启动 MCP Server（端口 8090）

```bash
mvn -pl showcase-spring-ai/showcase-spring-ai-mcp/mcp-mysql-server spring-boot:run
```

启动成功后，可见如下端点：

- SSE 订阅端点：`GET  http://localhost:8090/sse`
- MCP 消息端点：`POST http://localhost:8090/mcp/messages`

### 3. 启动 MCP Client（另开一个终端）

```bash
mvn -pl showcase-spring-ai/showcase-spring-ai-mcp/mcp-mysql-client spring-boot:run
```

控制台将依次输出：

1. 已发现的 MCP Tool 列表（含名称与描述）；
2. `listTables` → 当前库中的表名；
3. `describeTable(t_user)` → t_user 表结构；
4. `executeSelect(...)` → 用户与订单的关联查询结果。

### 4. （可选）启动 LLM 示例：让大模型自己调 MCP（端口 8092）

前置条件：本机已安装 [Ollama](https://ollama.com)，并 pull 一个**支持 tools / function-calling** 的模型，例如：

```bash
ollama pull qwen2.5:7b      # 或 llama3.1:8b、qwen3:8b 等
ollama serve                # 默认监听 11434
```

启动 LLM 应用：

```bash
mvn -pl showcase-spring-ai/showcase-spring-ai-mcp/mcp-mysql-llm spring-boot:run
```

然后用自然语言提问，由模型自己决定何时调用 `listTables` / `describeTable` / `executeSelect`：

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
mcp-mysql-server  →  JdbcTemplate  →  MySQL/H2
```

### 5. （可选）启动 MCP Gateway（端口 8095）

先保持 `mcp-mysql-server` 运行，再启动网关：

```bash
mvn -pl showcase-spring-ai-base/showcase-spring-ai-base-mcp/mcp-gateway spring-boot:run
```

默认 API Key 为 `demo-gateway-key`，默认聚合 `mcp-mysql-server`，并把工具暴露为：

- `mysql.listTables`
- `mysql.describeTable`
- `mysql.executeSelect`

运维端点：

```bash
curl -H 'X-API-Key: demo-gateway-key' http://localhost:8095/admin/upstreams
curl -H 'X-API-Key: demo-gateway-key' http://localhost:8095/admin/tools
curl -X POST -H 'X-API-Key: demo-gateway-key' http://localhost:8095/admin/refresh
```

详见 `mcp-gateway/README.md`。

## 工具一览

| Tool | 说明 | 关键参数 |
|---|---|---|
| `listTables` | 列出当前数据库所有表 | 无 |
| `describeTable` | 查看指定表结构 | `tableName` |
| `executeSelect` | 执行只读 SQL（仅 SELECT/SHOW/DESC） | `sql`、`maxRows`（可选，默认 100，最大 500） |

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
mvn -pl showcase-spring-ai/showcase-spring-ai-mcp/mcp-mysql-server \
    spring-boot:run \
    -Dspring-boot.run.profiles=mysql \
    -Dspring-boot.run.jvmArguments="-Dspring.datasource.url=jdbc:mysql://127.0.0.1:3306/your_db -Dspring.datasource.username=root -Dspring.datasource.password=xxx"
```

## 模块结构

```
showcase-spring-ai-mcp
├── pom.xml                       # 聚合 + spring-ai-bom
├── mcp-mysql-server              # MCP Server（SSE）
│   └── src/main
│       ├── java/.../server
│       │   ├── McpMysqlServerApplication.java
│       │   └── tool/MysqlQueryTools.java
│       └── resources
│           ├── application.yml
│           └── db/{schema.sql, data.sql}
├── mcp-mysql-client              # MCP Client（SSE）
│   └── src/main
│       ├── java/.../client
│       │   ├── McpMysqlClientApplication.java
│       │   └── runner/McpClientRunner.java
│       └── resources/application.yml
└── mcp-mysql-llm                 # LLM + MCP（Ollama + ChatClient）
    └── src/main
        ├── java/.../llm
        │   ├── McpMysqlLlmApplication.java
        │   ├── config/ChatClientConfig.java
        │   └── controller/AskController.java
        └── resources/application.yml
```
