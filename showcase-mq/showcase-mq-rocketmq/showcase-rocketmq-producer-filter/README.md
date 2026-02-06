# RocketMQ 消息生产者拦截器

本项目实现了完整的RocketMQ消息生产者拦截器体系，提供了消息验证、标签过滤、统计监控、加密处理等功能。

## 功能特性

### 1. 消息验证拦截器 (MessageValidationInterceptor)
- **优先级**: 100 (最高优先级)
- **功能**: 
  - 验证消息格式和内容
  - 检查消息大小限制
  - 检测敏感词汇
  - 验证JSON格式
- **配置**: 支持自定义消息大小限制

### 2. 消息加密拦截器 (MessageEncryptionInterceptor)
- **优先级**: 150
- **功能**:
  - 自动识别敏感消息
  - AES加密算法
  - 支持Base64编码
  - 可配置加密密钥
- **配置**: 支持开关控制，默认关闭

### 3. 消息标签过滤拦截器 (MessageTagFilterInterceptor)
- **优先级**: 200
- **功能**:
  - 自动生成消息标签
  - 根据内容分类消息
  - 添加消息来源标识
  - 支持自定义标签映射
- **配置**: 支持自定义标签规则

### 4. 消息统计拦截器 (MessageStatisticsInterceptor)
- **优先级**: 300 (最低优先级)
- **功能**:
  - 统计消息发送数量
  - 监控发送性能
  - 按主题和标签分类统计
  - 支持统计重置
- **配置**: 支持统计间隔和保留时间配置

## 拦截器执行顺序

```
消息发送前: 验证 → 加密 → 标签过滤 → 统计
消息发送后: 统计 → 标签过滤 → 加密 → 验证
异常处理: 统计 → 标签过滤 → 加密 → 验证
```

## 快速开始

### 1. 配置拦截器

在 `application.yml` 中配置拦截器参数：

```yaml
demo:
  rocketmq:
    interceptor:
      validation:
        enabled: true
        message-size:
          max-size: 4194304  # 4MB
          min-size: 1
      encryption:
        enabled: false
        key: "your-encryption-key"
      tag-filter:
        enabled: true
      statistics:
        enabled: true
        output-interval: 100
```

### 2. 使用拦截器

拦截器会自动集成到消息发送流程中：

```java
@Autowired
private MessageProducerComponent messageProducerComponent;

// 发送消息，拦截器会自动执行
SendResult result = messageProducerComponent.sendDelayMessage("消息内容", "hashKey");
```

### 3. 监控拦截器状态

通过REST API监控拦截器状态：

```bash
# 获取拦截器状态
GET /api/rocketmq/interceptor/status

# 获取统计信息
GET /api/rocketmq/interceptor/statistics

# 重置统计信息
POST /api/rocketmq/interceptor/statistics/reset

# 获取配置信息
GET /api/rocketmq/interceptor/config

# 健康检查
GET /api/rocketmq/interceptor/health
```

## API接口说明

### 拦截器状态接口

- **GET** `/api/rocketmq/interceptor/status`
  - 返回所有拦截器的状态信息
  - 包含消息统计、配置状态等

- **GET** `/api/rocketmq/interceptor/statistics`
  - 返回详细的统计信息
  - 包含消息数量、成功率、平均耗时等

- **POST** `/api/rocketmq/interceptor/statistics/reset`
  - 重置所有统计信息
  - 返回操作结果

- **GET** `/api/rocketmq/interceptor/config`
  - 返回当前拦截器配置
  - 包含各拦截器的开关状态

- **GET** `/api/rocketmq/interceptor/list`
  - 返回所有拦截器列表
  - 包含优先级和执行顺序

- **GET** `/api/rocketmq/interceptor/health`
  - 健康检查接口
  - 返回拦截器系统状态

## 自定义拦截器

### 1. 实现接口

```java
@Component
public class CustomInterceptor implements MessageInterceptor {
    
    @Override
    public Message beforeSend(Message message) {
        // 消息发送前处理
        return message;
    }
    
    @Override
    public void afterSend(Message message, SendResult sendResult) {
        // 消息发送后处理
    }
    
    @Override
    public void onException(Message message, Throwable throwable) {
        // 异常处理
    }
    
    @Override
    public int getOrder() {
        return 250; // 设置优先级
    }
}
```

### 2. 自动注册

Spring Boot会自动扫描并注册实现了 `MessageInterceptor` 接口的Bean，拦截器链会按优先级顺序执行。

## 配置说明

### 消息验证配置

```yaml
validation:
  enabled: true
  message-size:
    max-size: 4194304  # 最大消息大小(字节)
    min-size: 1        # 最小消息大小(字节)
```

### 加密配置

```yaml
encryption:
  enabled: false       # 是否启用加密
  key: "your-key"     # 加密密钥
  algorithm: "AES"     # 加密算法
  message-types:       # 需要加密的消息类型
    - "financial"
    - "user"
  sensitive-keywords:  # 敏感关键词
    - "password"
    - "token"
```

### 统计配置

```yaml
statistics:
  enabled: true           # 是否启用统计
  output-interval: 100    # 统计输出间隔(消息数量)
  detailed-enabled: true  # 是否启用详细统计
  retention-hours: 24     # 统计信息保留时间(小时)
```

## 性能考虑

1. **拦截器顺序**: 高频拦截器放在前面，减少不必要的处理
2. **异常处理**: 单个拦截器异常不影响其他拦截器执行
3. **统计优化**: 使用原子操作和ThreadLocal避免性能影响
4. **配置缓存**: 配置信息在启动时加载，运行时只读

## 监控和告警

### 日志监控

- 拦截器执行日志
- 消息处理统计
- 异常和错误日志

### 指标监控

- 消息发送成功率
- 平均发送耗时
- 拦截器执行时间
- 消息大小分布

### 告警规则

- 消息发送失败率 > 5%
- 平均发送耗时 > 100ms
- 拦截器异常频率过高

## 故障排查

### 常见问题

1. **消息被拦截器阻止**
   - 检查消息格式和大小
   - 查看拦截器日志
   - 验证配置参数

2. **加密失败**
   - 检查加密密钥配置
   - 验证加密算法支持
   - 查看加密相关日志

3. **统计信息异常**
   - 检查统计拦截器状态
   - 验证配置参数
   - 查看统计相关日志

### 调试模式

启用DEBUG日志级别：

```yaml
logging:
  level:
    com.jdktomcat.showcase.rocketmq.producer.filter: DEBUG
```

## 扩展功能

### 1. 消息路由拦截器
- 根据消息内容自动路由到不同Topic
- 支持动态路由规则配置

### 2. 消息压缩拦截器
- 自动压缩大消息
- 支持多种压缩算法

### 3. 消息审计拦截器
- 记录消息发送历史
- 支持消息追踪和回放

### 4. 消息限流拦截器
- 控制消息发送频率
- 支持多种限流策略

## 版本历史

- **v1.0.0**: 基础拦截器框架
  - 消息验证拦截器
  - 消息加密拦截器
  - 消息标签过滤拦截器
  - 消息统计拦截器

## 贡献指南

欢迎提交Issue和Pull Request来改进这个项目。

## 许可证

本项目采用MIT许可证。
