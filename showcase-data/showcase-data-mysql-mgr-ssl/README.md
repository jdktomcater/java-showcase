# MySQL MGR SSL 本地验证模块

这个模块提供两部分能力：

1. `docker-compose` 起一个 3 节点 MySQL Group Replication 集群，并打开 TLS/SSL。
2. 一个本地 Spring Boot 服务，对每个节点做 SSL 和 MGR 状态校验，输出 JSON 结果。

## 目录

- `docker/compose.yml`: 3 节点 MGR 集群和一次性 bootstrap 服务
- `docker/conf/mysql*/my.cnf`: 每个节点的 MySQL + MGR + SSL 配置
- `docker/init/01-init.sql`: 初始化业务库、探针表、验证账号、复制账号
- `docker/scripts/generate-certs.sh`: 生成 CA、节点证书和客户端 truststore
- `src/main/...`: 本地验证服务

## 前置条件

- Docker / Docker Compose
- JDK 17
- Maven 3.9+
- `openssl`
- `keytool`

## 启动集群

在模块目录下执行：

```bash
./docker/scripts/generate-certs.sh
docker compose -f docker/compose.yml up -d
```

等待 `mgr-setup` 执行完成后，检查集群：

```bash
docker compose -f docker/compose.yml ps
docker compose -f docker/compose.yml logs mgr-setup
```

三个节点映射到本机的端口分别是：

- `mysql1 -> 33061`
- `mysql2 -> 33062`
- `mysql3 -> 33063`

默认账号：

- root: `root@123456`
- 验证服务账号: `app_user / App#123456`
- MGR distributed recovery 账号: `repl / Repl#123456`

## 启动验证服务

在模块目录下执行：

```bash
mvn spring-boot:run
```

验证接口：

```bash
curl http://127.0.0.1:8088/api/mysql/mgr/validate
```

## 结果怎么看

返回结果里重点看这些字段：

- `valid`: 总体是否通过
- `checks`: 集群级检查项
- `nodes[].sslSession.Ssl_version`
- `nodes[].sslSession.Ssl_cipher`
- `nodes[].variables.group_replication_ssl_mode`
- `nodes[].variables.group_replication_recovery_use_ssl`
- `nodes[].recoveryChannel.sslAllowed`
- `nodes[].localMember.state`

满足下面条件，基本可以判定本地 MGR + SSL 生效：

- JDBC 当前会话的 `Ssl_version`、`Ssl_cipher` 非空
- `group_replication_ssl_mode != DISABLED`
- `group_replication_recovery_use_ssl = ON`
- `recoveryChannel.sslAllowed = Yes`
- 所有节点 `localMember.state = ONLINE`
- 每个节点都能看到 3 个成员

## 手工 SQL 复核

```sql
SHOW STATUS LIKE 'Ssl_%';
SHOW VARIABLES LIKE 'group_replication%ssl%';
SELECT * FROM performance_schema.replication_group_members;
SELECT CHANNEL_NAME, SSL_ALLOWED, TLS_VERSION, SSL_CA_FILE
FROM performance_schema.replication_connection_configuration
WHERE CHANNEL_NAME = 'group_replication_recovery';
```

## 说明

- 这里把 `require_secure_transport=ON` 也打开了，避免误连明文通道。
- `group_replication_ssl_mode=VERIFY_IDENTITY` 用于组内通信。
- distributed recovery 另外显式设置了 `group_replication_recovery_use_ssl=ON` 和 CA 校验。
- 验证服务默认使用 `VERIFY_CA` 连接本机映射端口；如需自定义证书路径，可覆盖 `SHOWCASE_MGR_TRUSTSTORE_URL` 等环境变量。
