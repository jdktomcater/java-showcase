# showcase-data-driver

基于 Spring Boot 2.7.16、Druid、MyBatis 的 MySQL 驱动版本查询影响对比模块。

## 目标

固定连接池、SQL、数据量和查询类型，只切换 MySQL JDBC 驱动版本，对比查询耗时差异。

当前内置两组 Maven Profile：

- `mysql5` -> `mysql-connector-java:5.1.49`
- `mysql8` -> `mysql-connector-java:8.0.26`

## 准备数据库

默认连接串：

```text
jdbc:mysql://127.0.0.1:3306/showcase_driver?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&cachePrepStmts=true&useServerPrepStmts=true
```

也可以通过环境变量覆盖：

```bash
export MYSQL_URL='jdbc:mysql://127.0.0.1:3306/showcase_driver?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&cachePrepStmts=true&useServerPrepStmts=true'
export MYSQL_USERNAME='root'
export MYSQL_PASSWORD='123456'
```

应用启动后会自动建表并写入基准数据。

## 运行方式

对比 MySQL 5 驱动：

```bash
mvn -pl showcase-data-driver -Pmysql5 spring-boot:run -Dspring-boot.run.profiles=mysql5
```

对比 MySQL 8 驱动：

```bash
mvn -pl showcase-data-driver -Pmysql8 spring-boot:run -Dspring-boot.run.profiles=mysql8
```

## 输出内容

启动后会输出：

- 当前 JDBC 驱动名称和版本
- 数据库版本
- 基准数据量
- 4 类查询场景的 `avg / p95 / max / qps`

查询场景包括：

- `point-select-by-id`
- `status-page-query`
- `created-at-window-scan`
- `status-amount-aggregate`

建议连续执行两次命令，并把日志里的同名场景指标做横向对比。
