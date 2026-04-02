# 代码影响链路依赖分析 - 使用指南

## 一、架构设计

### 1.1 核心概念

#### 入口点 (Entry Point)
代码执行的起始位置，分为 5 种类型：
- **HTTP**: REST API 接口
- **RPC**: 远程服务调用接口
- **MQ**: 消息队列消费者
- **SCHEDULED**: 定时任务
- **EVENT**: 事件监听器

#### 影响链路 (Impact Chain)
从入口点开始，通过方法调用、类依赖等关系形成的调用链。

#### 影响图 (Impact Graph)
使用图结构表示的影响链路，支持可视化展示。

### 1.2 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                      应用层 (Application)                    │
├─────────────────────────────────────────────────────────────┤
│  REST API Controller                                        │
│  /api/impact/*                                              │
├─────────────────────────────────────────────────────────────┤
│  服务层 (Service Layer)                                     │
│  ┌─────────────────┐  ┌──────────────────┐                 │
│  │ ImpactChain     │  │ GraphExport      │                 │
│  │ Analyzer        │  │ Service          │                 │
│  └─────────────────┘  └──────────────────┘                 │
│  ┌─────────────────┐  ┌──────────────────┐                 │
│  │ EntryPoint      │  │ JdtEntryPoint    │                 │
│  │ Scanner         │  │ Analyzer         │                 │
│  └─────────────────┘  └──────────────────┘                 │
├─────────────────────────────────────────────────────────────┤
│  数据层 (Repository Layer)                                  │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ ImpactChainRepository (Neo4j)                       │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

## 二、快速开始

### 2.1 环境准备

```bash
# 1. 启动 Neo4j
docker run -d --name neo4j-impact \
  -p 7474:7474 \
  -p 7687:7687 \
  -e NEO4J_AUTH=neo4j/password \
  neo4j:4.4

# 2. 配置环境变量
export CODE_IMPACT_REPO_ROOT=/path/to/your/project
```

### 2.2 启动应用

```bash
cd showcase-spring-ai-impact-analysis
mvn clean install
mvn spring-boot:run
```

### 2.3 执行分析

```bash
# 执行完整分析
curl -X POST http://localhost:8082/api/impact/analyze

# 查看入口点列表
curl http://localhost:8082/api/impact/entry-points

# 查询特定入口点的影响链
curl -X POST http://localhost:8082/api/impact/chain \
  -H "Content-Type: application/json" \
  -d '{
    "entryPointId": "HTTP:com.example.Controller#method()",
    "depth": 5
  }'
```

## 三、详细功能说明

### 3.1 入口点检测

#### 自动检测的注解

**HTTP 入口点:**
- `@RestController` + `@RequestMapping`
- `@Controller` + `@RequestMapping`
- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`, `@PatchMapping`

**RPC 入口点:**
- `@DubboService`
- `@Service` (Dubbo)
- `@GrpcService`

**MQ 入口点:**
- `@RabbitListener`
- `@KafkaListener`
- `@RocketMQMessageListener`

**定时任务:**
- `@Scheduled`

**事件驱动:**
- `@EventListener`
- `@TransactionalEventListener`

#### 自定义注解

可以在代码中添加自定义注解以增强检测精度：

```java
import com.jdktomcat.showcase.ai.impact.analysis.annotation.HttpEntry;

public class OrderController {
    
    @HttpEntry(
        paths = {"/api/orders/create"},
        methods = {"POST"},
        name = "创建订单接口"
    )
    public OrderResponse createOrder(@RequestBody OrderRequest request) {
        // ...
    }
}
```

### 3.2 影响链构建

系统会自动构建以下类型的依赖关系：

1. **DECLARES**: 类声明方法
2. **CALLS**: 方法调用方法
3. **DEPENDS_ON**: 类依赖其他类
4. **EXTENDS**: 类继承
5. **IMPLEMENTS**: 接口实现
6. **INJECTS**: 依赖注入
7. **INVOKES**: 方法调用（跨层）
8. **TRIGGERS**: 事件触发

### 3.3 查询功能

#### 按类型查询入口点

```bash
# 查询所有 HTTP 入口点
curl "http://localhost:8082/api/impact/entry-points/by-type?type=HTTP"

# 查询所有 MQ 入口点
curl "http://localhost:8082/api/impact/entry-points/by-type?type=MQ"
```

#### 深度可配置的链路查询

```bash
# 查询 3 层深度的影响链
curl -X POST http://localhost:8082/api/impact/chain \
  -H "Content-Type: application/json" \
  -d '{
    "entryPointId": "HTTP:com.example.OrderController#createOrder(OrderRequest)",
    "depth": 3
  }'
```

### 3.4 可视化导出

#### DOT 格式 (Graphviz)

```bash
# 导出单个入口点的图
curl "http://localhost:8082/api/impact/export/dot?entryPointId=HTTP:xxx&depth=5" \
  -o graph.dot

# 使用 Graphviz 渲染
dot -Tpng graph.dot -o graph.png

# 导出完整图
curl "http://localhost:8082/api/impact/export/dot/full" -o full-graph.dot
```

#### JSON 格式 (D3.js)

```bash
curl "http://localhost:8082/api/impact/export/json?entryPointId=HTTP:xxx&depth=5" \
  -o graph.json
```

配合 D3.js 进行 Web 可视化：

```html
<div id="graph"></div>
<script>
fetch('graph.json')
  .then(res => res.json())
  .then(data => {
    // 使用 D3.js 渲染力导向图
    d3.select("#graph").append("svg")
      .datum(data)
      // ... D3 代码
  });
</script>
```

#### Mermaid 格式

```bash
curl "http://localhost:8082/api/impact/export/mermaid?entryPointId=HTTP:xxx&depth=5" \
  -o graph.mmd
```

在 Markdown 文档中使用：

```markdown
## 订单创建接口影响链

```mermaid
graph TB
  OrderController[OrderController (METHOD)]
  OrderService[OrderService (TYPE)]
  OrderRepository[OrderRepository (TYPE)]
  
  OrderController -->|DEPENDS_ON| OrderService
  OrderService -->|DEPENDS_ON| OrderRepository
```
```

#### PlantUML 格式

```bash
curl "http://localhost:8082/api/impact/export/plantuml?entryPointId=HTTP:xxx&depth=5" \
  -o graph.puml
```

## 四、实际应用场景

### 4.1 代码变更影响评估

**场景**: 修改 `OrderService.createOrder()` 方法前，评估影响范围。

**步骤**:
1. 查询影响链
```bash
curl -X POST http://localhost:8082/api/impact/chain \
  -H "Content-Type: application/json" \
  -d '{
    "entryPointId": "METHOD:com.example.OrderService#createOrder(OrderRequest)",
    "depth": 5
  }'
```

2. 导出可视化图
```bash
curl "http://localhost:8082/api/impact/export/mermaid?entryPointId=METHOD:xxx" \
  -o impact.mmd
```

3. 分析结果，确定需要回归测试的范围

### 4.2 微服务依赖分析

**场景**: 分析微服务之间的调用关系。

**步骤**:
1. 获取所有 HTTP 入口点
```bash
curl "http://localhost:8082/api/impact/entry-points/by-type?type=HTTP"
```

2. 按模块分组分析
```cypher
// Neo4j Cypher 查询
MATCH (node:CodeNode)
WHERE node.module IS NOT NULL
RETURN node.module, count(*) AS nodeCount
ORDER BY nodeCount DESC
```

3. 导出完整依赖图进行可视化分析

### 4.3 定时任务梳理

**场景**: 梳理系统中的所有定时任务及其影响。

**步骤**:
1. 查询所有定时任务入口点
```bash
curl "http://localhost:8082/api/impact/entry-points/by-type?type=SCHEDULED"
```

2. 分析每个定时任务的影响链
```bash
# 对每个定时任务执行
curl -X POST http://localhost:8082/api/impact/chain \
  -H "Content-Type: application/json" \
  -d '{"entryPointId": "SCHEDULED:xxx", "depth": 5}'
```

### 4.4 事件驱动架构分析

**场景**: 理解事件发布和订阅关系。

**步骤**:
1. 查询所有事件监听器
```bash
curl "http://localhost:8082/api/impact/entry-points/by-type?type=EVENT"
```

2. 使用 Cypher 查询事件流
```cypher
MATCH (ep:EntryPoint {type: 'EVENT'})-[:POINTS_TO]->(listener)
MATCH (listener)-[:TRIGGERS*1..3]->(target)
RETURN ep.methodName, collect(DISTINCT target.fqn) AS triggeredNodes
```

## 五、Neo4j 高级查询

### 5.1 常用查询

```cypher
// 1. 查询所有入口点及其类型
MATCH (ep:EntryPoint)
RETURN ep.type AS type, count(*) AS count
ORDER BY count DESC

// 2. 查询被调用最多的方法
MATCH (caller:CodeNode)-[:CALLS]->(callee:CodeNode)
WHERE callee.type = 'METHOD'
RETURN callee.fqn, count(caller) AS callCount
ORDER BY callCount DESC
LIMIT 10

// 3. 查询跨模块依赖
MATCH (source:CodeNode)-[:RELATES]->(target:CodeNode)
WHERE source.module <> target.module 
  AND source.module IS NOT NULL 
  AND target.module IS NOT NULL
RETURN source.module, target.module, count(*) AS depCount
ORDER BY depCount DESC

// 4. 查询某个类的完整依赖树
MATCH (start:CodeNode {fqn: 'com.example.OrderService'})
MATCH p = (start)-[:RELATES*1..5]->(target)
RETURN p

// 5. 查询循环依赖
MATCH (a:CodeNode {type: 'TYPE'})-[:DEPENDS_ON*2..5]->(a)
RETURN a.fqn
```

### 5.2 性能优化索引

```cypher
// 创建索引
CREATE INDEX entry_point_id FOR (ep:EntryPoint) ON (ep.id)
CREATE INDEX entry_point_type FOR (ep:EntryPoint) ON (ep.type)
CREATE INDEX code_node_id FOR (n:CodeNode) ON (n.id)
CREATE INDEX code_node_type FOR (n:CodeNode) ON (n.type)
CREATE INDEX code_node_fqn FOR (n:CodeNode) ON (n.fqn)
CREATE INDEX code_node_module FOR (n:CodeNode) ON (n.module)

// 创建关系索引
CREATE INDEX rel_type FOR ()-[r:RELATES]-() ON (r.type)
```

## 六、最佳实践

### 6.1 增量分析

对于大型代码库，建议只分析变更的文件：

```java
// 自定义分析策略
public class IncrementalAnalysisStrategy {
    public void analyzeChangedFiles(List<Path> changedFiles) {
        for (Path file : changedFiles) {
            // 只分析变更的文件
            List<EntryPoint> entryPoints = analyzer.analyze(file);
            repository.saveEntryPoints(entryPoints);
        }
    }
}
```

### 6.2 CI/CD 集成

在 CI/CD 流水线中集成影响分析：

```yaml
# .github/workflows/impact-analysis.yml
name: Impact Analysis

on:
  pull_request:
    branches: [main]

jobs:
  impact-analysis:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Run Impact Analysis
        run: |
          curl -X POST http://neo4j:8082/api/impact/analyze \
            -d "repo=${{ github.sha }}"
      
      - name: Export Impact Graph
        run: |
          curl "http://neo4j:8082/api/impact/export/json?mode=full" \
            -o impact-graph.json
      
      - name: Upload Artifact
        uses: actions/upload-artifact@v3
        with:
          name: impact-graph
          path: impact-graph.json
```

### 6.3 性能调优

```yaml
# application.properties 性能配置
spring:
  neo4j:
    uri: neo4j://localhost:7687
    authentication:
      username: neo4j
      password: password

# 连接池配置
neo4j:
  driver:
    max-connection-pool-size: 50
    connection-acquisition-timeout: 60s

# 分析配置
app:
  impact-analysis:
    # 并行处理文件数
    parallelism: 4
    # 批量保存大小
    batch-size: 1000
```

## 七、故障排查

### 7.1 常见问题

**问题 1: Neo4j 连接失败**
```
解决方案:
1. 检查 Neo4j 是否运行: docker ps | grep neo4j
2. 检查端口: netstat -tlnp | grep 7687
3. 检查配置：neo4j.uri, neo4j.username, neo4j.password
```

**问题 2: 分析结果为空**
```
解决方案:
1. 检查 repo-root 配置是否正确
2. 确认源代码路径可访问
3. 开启 DEBUG 日志查看详细过程
```

**问题 3: 内存不足**
```
解决方案:
1. 增加 JVM 堆内存：-Xmx4g
2. 分批处理大型代码库
3. 使用增量分析
```

### 7.2 日志配置

```yaml
logging:
  level:
    com.jdktomcat.showcase.ai.impact.analysis: DEBUG
    org.neo4j: INFO
    org.eclipse.jdt: WARN
```

## 八、扩展开发

### 8.1 添加新的入口点类型

```java
// 1. 定义新的注解
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GrpcEntry {
    String service() default "";
    String method() default "";
}

// 2. 添加入口点类型枚举
public enum EntryPointType {
    HTTP, RPC, MQ, SCHEDULED, EVENT, GRPC  // 新增 GRPC
}

// 3. 在 EntryPointScanner 中添加检测逻辑
private EntryPointType determineEntryPointType(...) {
    if (methodAnnotations.contains("GrpcEntry")) {
        return EntryPointType.GRPC;
    }
    // ...
}
```

### 8.2 自定义关系类型

```java
// 添加新的关系类型
public enum RelationType {
    // 现有类型...
    PUBLISHES,    // 发布消息
    CONSUMES,     // 消费消息
    ACCESSES      // 访问数据
}
```

## 九、总结

代码影响链路依赖分析系统提供了：

✅ **全面的入口点检测** - 支持 5 种数据流类型
✅ **精确的 AST 分析** - 基于 Eclipse JDT
✅ **高效的图存储** - Neo4j 图数据库
✅ **灵活的查询 API** - RESTful 接口
✅ **多样的可视化** - DOT, JSON, Mermaid, PlantUML
✅ **易于集成** - 可嵌入 CI/CD 流程

通过本系统，可以有效进行代码变更影响评估、架构依赖分析、问题排查等工作。
