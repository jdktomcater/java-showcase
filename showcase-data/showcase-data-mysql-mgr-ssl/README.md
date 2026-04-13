# MySQL MGR SSL 本地验证模块

这个模块提供三部分能力：

1. `docker-compose` 起一个 3 节点 MySQL Group Replication 集群，并打开 TLS/SSL。
2. 一个 F5 NGINX 风格的 TCP 代理层，对外暴露 MGR 单主集群的读写代理端口。
3. 一个本地 Spring Boot 服务，使用 Druid 连接池和 MyBatis-Plus 对业务表做读写验证，同时对每个节点和代理端口做 SSL / MGR 状态校验并输出 JSON 结果。

## 目录

- `docker/compose.yml`: 3 节点 MGR 集群和一次性 bootstrap 服务
- `docker/f5-proxy/*`: F5 NGINX 风格的 MySQL MGR 代理镜像和自动发现脚本
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

F5 代理层暴露的端口：

- `f5-mgr-proxy rw -> 13306`
- `f5-mgr-proxy ro -> 13307`
- `f5-mgr-proxy status -> 18080`

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

也可以直接通过代理测试：

```bash
mysql --ssl-mode=VERIFY_CA --ssl-ca=./docker/certs/ca/ca.pem -h127.0.0.1 -P13306 -uapp_user -p
mysql --ssl-mode=VERIFY_CA --ssl-ca=./docker/certs/ca/ca.pem -h127.0.0.1 -P13307 -uapp_user -p
curl http://127.0.0.1:18080/proxy-status
```

## 结果怎么看

返回结果里重点看这些字段：

- `valid`: 总体是否通过
- `checks`: 集群级检查项
- `mybatisPlusProbe`
- `proxies[]`
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
- `f5-rw` 命中可写主节点
- `f5-ro` 命中只读从节点
- `mybatisPlusProbe.valid = true`

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
- F5 代理层这里用的是基于 F5 NGINX 体系的 `stream` TCP 代理思路，`rw` 端口动态指向 PRIMARY，`ro` 端口动态指向 SECONDARY。
- Spring Boot 默认业务数据源走 `f5-rw`，底层连接池使用 Druid，`ssl_probe` 的读写校验通过 MyBatis-Plus 完成。
