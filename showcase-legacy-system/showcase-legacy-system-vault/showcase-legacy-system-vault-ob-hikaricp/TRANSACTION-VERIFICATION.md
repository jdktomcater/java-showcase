# 数据源切换与嵌套事务验证

测试类：DataSourceSwitchTransactionTest。使用真实 Hikari 连接池、模块内的 SwitchableDataSource 和 Spring DataSourceTransactionManager。
TransactionTemplate 确保传播行为实际生效，避免同类调用 @Transactional 绕过代理。

## 运行

在仓库目录执行：

```powershell
mvn -f showcase-legacy-system/showcase-legacy-system-vault/showcase-legacy-system-vault-ob-hikaricp/pom.xml -Dtest=DataSourceSwitchTransactionTest test
```

默认使用隔离的 H2 数据库，不启动应用，也不读取 application.yaml 或连接 Vault。
IDE 使用前需重新加载 Maven 依赖。

## 场景和断言

| 场景 | 预期连接 | 最终数据 |
| --- | --- | --- |
| 外层写入后切换，NESTED 回滚，外层提交 | 内外层保持同一旧连接，有保存点 | 保留外层 1、3，撤销内层 2 |
| NESTED 建立保存点后切换，内层提交，外层回滚 | 保持同一旧连接 | 所有写入撤销 |
| 切换后调用 REQUIRED | 加入外层事务，保持旧连接 | 内外层 1、2 一起提交 |
| 切换后调用 REQUIRES_NEW，外层最终回滚 | 内层新池连接，返回后外层恢复旧连接 | 只保留独立提交的内层 2 |

首个场景还检查外层结束后旧池活跃连接归零，关闭旧池后新事务可继续写入。

## OceanBase 实测

同一测试可通过 JVM 系统属性连接 OceanBase：
- tx.probe.jdbc-url：与 application.yaml 相同的 JDBC URL。
- tx.probe.old.username / tx.probe.old.password：旧池账号和密码。
- tx.probe.new.username / tx.probe.new.password：新池账号和密码。

用户名填写完整 tenant/cluster 后缀。两个账号须访问同一测试库，并有创建、读写、删除测试表权限。
可在 IDEA 的 JUnit 运行配置 VM options 中设置这些 -D 属性。
每个场景创建随机命名的 tx_probe_* 表，结束时删除该表；DDL 在事务场景之外执行。
不要将真实密码提交到仓库。

## 结论边界

这些测试在事务内确定性调用 switchTo，不等待 Vault 租期，验证池切换与事务传播的交互。
不验证 MultiDbLeaseListener 的事件处理、异步回收线程、真实账号吊销或并发借连接竞态。
从当前代码看，旧池回收等待 activeConnections 归零；REQUIRES_NEW 挂起外层时，外层旧连接仍被借用。
但账号过期若导致 OceanBase 服务端终止旧会话，仍可能使旧事务失败，需要真实 Vault/OceanBase 联动验证。

验证期间可另行启动应用，先建立外层事务、记录 CONNECTION_ID()/CURRENT_USER()，在事务保持期间触发实际凭证轮换，
再执行 NESTED/REQUIRES_NEW 并记录内外层会话；事务结束后从独立连接检查最终记录。
不要仅以“接口无异常”作为通过标准，应同时确认保存点回滚、外层回滚及独立内层提交的结果。
