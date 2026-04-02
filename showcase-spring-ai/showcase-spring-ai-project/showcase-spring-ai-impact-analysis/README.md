# Code Impact Chain Dependency Analysis

代码影响链路依赖分析系统 - 用于多层面数据流的代码影响分析

## 概述

本模块提供了一个全面的代码影响链路分析系统，支持以下 5 种数据流入口的依赖分析：

1. **HTTP 接口** - REST Controllers, Request Mappings
2. **RPC 接口** - Dubbo Services, gRPC Services
3. **MQ 消费驱动** - RabbitMQ, Kafka, RocketMQ Listeners
4. **定时任务** - @Scheduled 任务
5. **服务事件驱动** - Application Events, Event Listeners

## 核心功能

### 1. 入口点自动检测

- 基于 Eclipse JDT AST 解析的精确检测
- 支持 Spring Framework 标准注解
- 支持自定义注解 (`@HttpEntry`, `@RpcEntry`, `@MqEntry`, `@ScheduledEntry`, `@EventEntry`)
- 支持 Dubbo、Kafka、RabbitMQ 等常用框架注解

### 2. 影响链路构建

- Controller/Service/Repository 多层依赖分析
- 方法调用链追踪
- 类依赖关系分析
- 跨模块依赖追踪

### 3. 图存储与查询

- Neo4j 图数据库存储
- 高效的链路查询
- 支持深度可配置的遍历

### 4. 可视化导出

- DOT 格式 (Graphviz)
- JSON 格式 (D3.js)
- Mermaid 格式 (文档嵌入)
- PlantUML 格式

## 快速开始

### 前置条件

1. Java 17+
2. Maven 3.8+
3. Neo4j 4.4+ 或 Neo4j Aura

### 安装 Neo4j

```bash
# 使用 Docker 运行 Neo4j
docker run -d \
  --name neo4j \
  -p 7474:7474 \
  -p 7687:7687 \
  -e NEO4J_AUTH=neo4j/password \
  neo4j:4.4
```

### 配置

编辑 `application.yml`:

```yaml
# Neo4j 配置
neo4j.uri=neo4j://localhost:7687
neo4j.username=neo4j
neo4j.password=password

# 代码仓库根目录
app.impact-analysis.repo-root=/path/to/your/codebase
```

### 运行

```bash
cd showcase-spring-ai-impact-analysis
mvn spring-boot:run
```

## API 参考

### 1. 执行分析

```bash
POST /api/impact/analyze
```

执行完整的代码影响分析，包括：
- 扫描所有 Java 文件
- 检测入口点
- 构建影响链路
- 存储到 Neo4j

**响应示例:**
```json
{
  "success": true,
  "entryPointCount": 45,
  "nodeCount": 523,
  "relationCount": 892,
  "message": "Analysis completed successfully"
}
```

### 2. 获取所有入口点

```bash
GET /api/impact/entry-points
```

**响应示例:**
```json
{
  "success": true,
  "count": 45,
  "entryPoints": [
    {
      "id": "HTTP:com.example.OrderController#createOrder(OrderRequest)",
      "type": "HTTP",
      "className": "com.example.OrderController",
      "methodName": "createOrder",
      "methodSignature": "com.example.OrderController#createOrder(OrderRequest)",
      "filePath": "order-service/src/main/java/com/example/OrderController.java",
      "module": "order-service",
      "lineNumber": 42,
      "metadata": "PostMapping;"
    }
  ]
}
```

### 3. 按类型获取入口点

```bash
GET /api/impact/entry-points/by-type?type=HTTP
```

支持的类型：`HTTP`, `RPC`, `MQ`, `SCHEDULED`, `EVENT`

### 4. 查询影响链路

```bash
POST /api/impact/chain
Content-Type: application/json

{
  "entryPointId": "HTTP:com.example.OrderController#createOrder(OrderRequest)",
  "depth": 5
}
```

**响应示例:**
```json
{
  "success": true,
  "entryPointId": "HTTP:com.example.OrderController#createOrder(OrderRequest)",
  "entryPointType": "HTTP",
  "depth": 5,
  "count": 12,
  "impactChain": [
    {
      "entryPointId": "HTTP:com.example.OrderController#createOrder(OrderRequest)",
      "entryPointType": "HTTP",
      "id": "TYPE:com.example.OrderService",
      "type": "TYPE",
      "name": "OrderService",
      "fqn": "com.example.OrderService",
      "filePath": "order-service/src/main/java/com/example/OrderService.java",
      "hops": 1,
      "relationTypes": ["DEPENDS_ON"]
    }
  ]
}
```

### 5. 获取影响图

```bash
POST /api/impact/graph
Content-Type: application/json

{
  "entryPointId": "HTTP:com.example.OrderController#createOrder(OrderRequest)",
  "mode": "impact",
  "depth": 5
}
```

**模式说明:**
- `impact`: 单个入口点的影响图
- `full`: 全部入口点的完整图（可按类型过滤）

### 6. 导出可视化图

#### DOT 格式 (Graphviz)
```bash
GET /api/impact/export/dot?entryPointId=HTTP:com.example.OrderController#createOrder(OrderRequest)&depth=5
```

#### JSON 格式 (D3.js)
```bash
GET /api/impact/export/json?entryPointId=HTTP:com.example.OrderController#createOrder(OrderRequest)&depth=5
```

#### Mermaid 格式
```bash
GET /api/impact/export/mermaid?entryPointId=HTTP:com.example.OrderController#createOrder(OrderRequest)&depth=5
```

#### PlantUML 格式
```bash
GET /api/impact/export/plantuml?entryPointId=HTTP:com.example.OrderController#createOrder(OrderRequest)&depth=5
```

## 自定义注解

### @HttpEntry

标记 HTTP 入口点：

```java
@RestController
@RequestMapping("/orders")
public class OrderController {
    
    @HttpEntry(paths = {"/create"}, methods = {"POST"})
    public OrderResponse createOrder(@RequestBody OrderRequest request) {
        // ...
    }
}
```

### @RpcEntry

标记 RPC 入口点：

```java
@DubboService
public class OrderServiceImpl implements OrderService {
    
    @RpcEntry(protocol = "DUBBO", service = "OrderService")
    public OrderResponse createOrder(OrderRequest request) {
        // ...
    }
}
```

### @MqEntry

标记 MQ 消费入口点：

```java
@Component
public class OrderMessageListener {
    
    @MqEntry(broker = "RABBITMQ", destinations = {"order.create.queue"})
    @RabbitListener(queues = "order.create.queue")
    public void handleOrderCreate(Message message) {
        // ...
    }
}
```

### @ScheduledEntry

标记定时任务入口点：

```java
@Component
public class OrderCleanupTask {
    
    @ScheduledEntry(cron = "0 0 2 * * ?")
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredOrders() {
        // ...
    }
}
```

### @EventEntry

标记事件驱动入口点：

```java
@Component
public class OrderEventListener {
    
    @EventEntry(eventTypes = {OrderCreatedEvent.class})
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        // ...
    }
}
```

## 架构图

### 系统架构

```
┌─────────────────────────────────────────────────────────┐
│                    Impact Analysis                       │
│                       Application                        │
├─────────────────────────────────────────────────────────┤
│  Controller Layer (REST API)                             │
│  /api/impact/*                                           │
├─────────────────────────────────────────────────────────┤
│  Service Layer                                           │
│  - ImpactChainAnalyzer                                   │
│  - EntryPointScanner                                     │
│  - JdtEntryPointAnalyzer                                 │
│  - GraphExportService                                    │
├─────────────────────────────────────────────────────────┤
│  Repository Layer                                        │
│  - ImpactChainRepository (Neo4j)                         │
├─────────────────────────────────────────────────────────┤
│  Domain Layer                                            │
│  - EntryPoint, ImpactChainNode, ImpactChainRelation      │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                      Neo4j Graph DB                      │
│  - EntryPoint nodes                                      │
│  - CodeNode nodes (TYPE, METHOD, etc.)                   │
│  - RELATES relationships                                 │
└─────────────────────────────────────────────────────────┘
```

### 数据流

```
Java Source Files
       │
       ▼
┌──────────────────┐
│ EntryPointScanner│ (Pattern matching)
└──────────────────┘
       │
       ▼
┌──────────────────────┐
│ JdtEntryPointAnalyzer│ (AST parsing)
└──────────────────────┘
       │
       ▼
┌──────────────────┐
│ ImpactChain      │ (Build chains)
│ Analyzer         │
└──────────────────┘
       │
       ▼
┌──────────────────┐
│ ImpactChain      │ (Persist)
│ Repository       │
└──────────────────┘
       │
       ▼
┌──────────────────┐
│ Neo4j            │
└──────────────────┘
```

## 使用场景

### 1. 代码变更影响分析

在修改某个方法前，查询其影响链路：

```bash
# 查询 OrderService.createOrder 的影响范围
curl -X POST http://localhost:8082/api/impact/chain \
  -H "Content-Type: application/json" \
  -d '{"entryPointId": "HTTP:com.example.OrderController#createOrder(OrderRequest)", "depth": 5}'
```

### 2. 微服务依赖分析

分析微服务之间的调用关系：

```bash
# 获取所有 HTTP 入口点
curl http://localhost:8082/api/impact/entry-points/by-type?type=HTTP

# 导出完整依赖图
curl http://localhost:8082/api/impact/export/json?mode=full -o full-graph.json
```

### 3. 定时任务影响分析

查看定时任务的调用链：

```bash
curl -X POST http://localhost:8082/api/impact/chain \
  -H "Content-Type: application/json" \
  -d '{"entryPointId": "SCHEDULED:com.example.OrderCleanupTask#cleanupExpiredOrders()", "depth": 3}'
```

### 4. 事件驱动架构分析

分析事件发布和订阅关系：

```bash
# 获取所有事件驱动入口点
curl http://localhost:8082/api/impact/entry-points/by-type?type=EVENT
```

## Neo4j Cypher 查询示例

### 查询所有 HTTP 入口点
```cypher
MATCH (ep:EntryPoint {type: 'HTTP'})
RETURN ep.id, ep.className, ep.methodName
ORDER BY ep.className
```

### 查询某个方法的完整调用链
```cypher
MATCH (ep:EntryPoint {id: 'HTTP:com.example.OrderController#createOrder(OrderRequest)'})-[:POINTS_TO]->(start:CodeNode)
MATCH p = (start)-[:RELATES*1..5]->(target:CodeNode)
RETURN p
```

### 查询被调用最多的方法
```cypher
MATCH (caller:CodeNode)-[:CALLS]->(callee:CodeNode)
WHERE callee.type = 'METHOD'
RETURN callee.fqn, count(caller) AS callCount
ORDER BY callCount DESC
LIMIT 10
```

### 查询跨模块依赖
```cypher
MATCH (source:CodeNode)-[:RELATES]->(target:CodeNode)
WHERE source.module <> target.module
RETURN source.module, target.module, count(*) AS depCount
ORDER BY depCount DESC
```

## 性能优化建议

1. **增量分析**: 仅分析变更的文件
2. **并行处理**: 使用多线程扫描文件
3. **缓存**: 缓存 AST 解析结果
4. **图索引**: 在 Neo4j 中为常用查询创建索引

```cypher
CREATE INDEX entry_point_id FOR (ep:EntryPoint) ON (ep.id)
CREATE INDEX code_node_id FOR (n:CodeNode) ON (n.id)
CREATE INDEX code_node_type FOR (n:CodeNode) ON (n.type)
```

## 故障排查

### 常见问题

1. **Neo4j 连接失败**
   ```
   检查 Neo4j 是否运行：docker ps | grep neo4j
   检查配置：neo4j.uri, neo4j.username, neo4j.password
   ```

2. **分析结果为空**
   ```
   检查 app.impact-analysis.repo-root 配置
   确认源代码路径可访问
   查看日志：logging.level.com.jdktomcat.showcase.ai.impact.analysis=DEBUG
   ```

3. **内存不足**
   ```
   增加 JVM 堆内存：-Xmx4g
   分批处理大型代码库
   ```

## 未来规划

- [ ] 支持更多 RPC 框架 (gRPC, Thrift)
- [ ] 支持更多 MQ 中间件 (Pulsar, ActiveMQ)
- [ ] 实时增量分析
- [ ] Web UI 可视化
- [ ] 与 CI/CD 集成
- [ ] AI 辅助影响分析

## 许可证

MIT License

## 贡献

欢迎提交 Issue 和 Pull Request！
