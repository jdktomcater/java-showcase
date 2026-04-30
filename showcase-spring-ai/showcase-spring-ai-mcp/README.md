# showcase-spring-ai-mcp

基于 [Spring AI 1.0.3](https://docs.spring.io/spring-ai/reference/) 的 **MCP（Model Context Protocol）** 基础调用示例。

通过一个最小可运行的「MySQL 只读查询工具」展示 MCP 的两端：

| 子模块 | 角色 | 说明 |
|---|---|---|
| `mcp-mysql-server` | **MCP Server** | 暴露 `listTables`、`describeTable`、`executeSelect` 三个 Tool，使用 SSE 传输（基于 Spring MVC） |
| `mcp-mysql-client` | **MCP Client** | 通过 SSE 连接到 Server，启动时自动列出工具并依次调用，将结果打印到控制台 |

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
└── mcp-mysql-client              # MCP Client（SSE）
    └── src/main
        ├── java/.../client
        │   ├── McpMysqlClientApplication.java
        │   └── runner/McpClientRunner.java
        └── resources/application.yml
```
