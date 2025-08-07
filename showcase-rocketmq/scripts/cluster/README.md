# RocketMQ 集群 Docker 部署

这是一个包含2个Broker主节点和2个从节点的RocketMQ集群配置。

## 集群架构

```
┌─────────────────┐    ┌─────────────────┐
│   Name Server   │    │   Console       │
│   (9876)        │    │   (8080)        │
└─────────────────┘    └─────────────────┘
         │
    ┌────┴────┐
    │         │
┌───▼───┐ ┌───▼───┐
│Master1│ │Master2│
│(10911)│ │(12911)│
└───┬───┘ └───┬───┘
    │         │
┌───▼───┐ ┌───▼───┐
│Slave1 │ │Slave2 │
│(11911)│ │(13911)│
└───────┘ └───────┘
```

## 服务端口映射

| 服务 | 容器端口 | 主机端口 | 说明 |
|------|----------|----------|------|
| Name Server | 9876 | 9876 | 命名服务 |
| Broker Master 1 | 10911 | 10911 | 主节点1 |
| Broker Slave 1 | 10911 | 11911 | 从节点1 |
| Broker Master 2 | 10911 | 12911 | 主节点2 |
| Broker Slave 2 | 10911 | 13911 | 从节点2 |
| Console | 8080 | 8080 | 管理控制台 |

## 快速开始

### 1. 启动集群

```bash
# 给启动脚本执行权限
chmod +x start-rocketmq-cluster.sh

# 启动集群
./start-rocketmq-cluster.sh
```

或者手动启动：

```bash
# 创建必要目录
mkdir -p logs/namesrv logs/broker-master-1 logs/broker-slave-1 logs/broker-master-2 logs/broker-slave-2
mkdir -p store/namesrv store/broker-master-1 store/broker-slave-1 store/broker-master-2 store/broker-slave-2
mkdir -p conf

# 启动服务
docker-compose -f rocketmq-cluster-docker-compose.yaml up -d
```

### 2. 检查集群状态

```bash
# 查看容器状态
docker-compose -f rocketmq-cluster-docker-compose.yaml ps

# 查看日志
docker-compose -f rocketmq-cluster-docker-compose.yaml logs -f
```

### 3. 访问管理控制台

打开浏览器访问：http://localhost:8080

默认登录信息：
- 用户名：admin
- 密码：admin

### 4. 停止集群

```bash
docker-compose -f rocketmq-cluster-docker-compose.yaml down
```

## 配置文件说明

### Broker 配置

每个Broker都有独立的配置文件，位于 `conf/` 目录下：

- `broker-master-1.conf` - 主节点1配置
- `broker-slave-1.conf` - 从节点1配置  
- `broker-master-2.conf` - 主节点2配置
- `broker-slave-2.conf` - 从节点2配置

### 关键配置参数

- `brokerClusterName`: 集群名称
- `brokerName`: Broker名称
- `brokerId`: Broker ID (0=master, 非0=slave)
- `brokerRole`: Broker角色 (ASYNC_MASTER/SLAVE)
- `namesrvAddr`: Name Server地址
- `listenPort`: 监听端口

## 集群特性

### 高可用性
- 2个主节点提供负载均衡
- 每个主节点都有对应的从节点提供故障转移
- 异步复制确保性能

### 消息可靠性
- 异步刷盘 (ASYNC_FLUSH)
- 异步复制 (ASYNC_MASTER)
- 消息持久化存储

### 扩展性
- 支持水平扩展
- 可以添加更多Broker节点
- 支持多集群部署

## 测试连接

### 使用官方工具测试

```bash
# 进入Name Server容器
docker exec -it rmqnamesrv bash

# 查看集群信息
sh mqadmin clusterList -n localhost:9876

# 查看Broker状态
sh mqadmin brokerStatus -n localhost:9876 -b broker-master-1
```

### Java 客户端连接示例

```java
// 生产者配置
DefaultMQProducer producer = new DefaultMQProducer("producer-group");
producer.setNamesrvAddr("localhost:9876");
producer.start();

// 消费者配置
DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("consumer-group");
consumer.setNamesrvAddr("localhost:9876");
consumer.subscribe("test-topic", "*");
```

## 故障排查

### 常见问题

1. **容器启动失败**
   ```bash
   # 查看容器日志
   docker logs rmqnamesrv
   docker logs rmqbroker-master-1
   ```

2. **Broker无法连接Name Server**
   - 检查网络连接
   - 确认Name Server已启动
   - 检查配置文件中的namesrvAddr

3. **磁盘空间不足**
   - 检查store目录大小
   - 调整fileReservedTime参数

### 日志位置

- Name Server: `./logs/namesrv/`
- Broker Master 1: `./logs/broker-master-1/`
- Broker Slave 1: `./logs/broker-slave-1/`
- Broker Master 2: `./logs/broker-master-2/`
- Broker Slave 2: `./logs/broker-slave-2/`

## 性能调优

### JVM 参数

当前配置：
- Name Server: `-Xms512m -Xmx512m -Xmn256m`
- Broker: `-Xms1g -Xmx1g -Xmn512m`

### 系统参数

- `sendMessageThreadPoolNums`: 发送消息线程池数量
- `pullMessageThreadPoolNums`: 拉取消息线程池数量
- `maxMessageSize`: 最大消息大小

## 监控

### 使用控制台监控
- 访问 http://localhost:8080
- 查看集群状态、Topic、Consumer等信息

### 使用命令行工具
```bash
# 查看集群信息
sh mqadmin clusterList -n localhost:9876

# 查看Topic信息
sh mqadmin topicList -n localhost:9876

# 查看Consumer信息
sh mqadmin consumerStatus -n localhost:9876 -g consumer-group
``` 