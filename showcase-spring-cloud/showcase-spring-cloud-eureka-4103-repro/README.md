# Eureka #4103 本地复现

验证 [spring-cloud-netflix #4103](https://github.com/spring-cloud/spring-cloud-netflix/issues/4103)：
心跳请求异常导致 transport 客户端被丢弃、重建时，
`RestTemplateEurekaHttpClient.shutdown()` 没有关闭独立创建的底层 HttpClient。
这里验证的是 RestTemplate + Apache HttpClient，不是 Jersey。

## 当前验证版本与结果

直接继承上级 POM 的依赖管理，没有降级或覆盖业务模块版本。
2026-09-22 在 Windows、测试 JVM JDK 17 下实测：

- Spring Boot BOM：3.2.0
- Spring Cloud BOM：2023.0.0
- spring-cloud-netflix-eureka-client：4.1.0
- eureka-client：2.0.1
- Apache HttpClient 5：5.2.1
- Spring Web：6.1.1

| 场景 | 创建客户端数 | 调用 shutdown 次数 | 观察时未关闭连接 | CLOSE_WAIT |
| --- | ---: | ---: | ---: | ---: |
| 12 次正常心跳，随后调用默认 shutdown | 1 | 1 | 1 | 0 |
| 5 轮正常心跳 + HTTP 500，然后服务端发送 FIN | 5 | 5 | 5 | 5 |
| 相同故障，shutdown 时显式销毁各自 request factory | 5 | 5 | 0 | 0 |

三项测试通过，表示预期的缺陷特征和对照结果均得到确认，不代表默认客户端没有缺陷。
所有观察结束后，测试显式清理底层客户端、监听 socket 和服务端工作线程。

## 执行

在仓库根目录执行（JDK 17、Maven）：

```bash
mvn -f showcase-spring-cloud/showcase-spring-cloud-eureka-4103-repro/pom.xml test
```

或者在 `showcase-spring-cloud` 目录执行：

```bash
mvn -Pissue-4103 -pl showcase-spring-cloud-eureka-4103-repro test
```

无需启动 Eureka Server、数据库、Vault 或 Docker。
模拟端点只监听 `127.0.0.1` 的随机端口。
复现模块通过父 POM 的 `issue-4103` profile 加入 reactor，默认业务构建不包含该模块。

IDEA 中勾选 Maven 的 `issue-4103` profile 并 Reload，
或将本模块的 `pom.xml` 添加为 Maven 项目，然后运行 `Eureka4103Test`。
可单独执行：

- `normalHeartbeatsReuseOneConnection`：正常连接复用基线。
- `heartbeatFailuresLeaveOldClientsOpen`：默认实现的异常与连接累积。
- `closingOwnedHttpClientPreventsAccumulation`：实验性的显式关闭对照。

输出位于本模块：

- `target/surefire-reports/`：JUnit 结果与逐轮客户端/连接计数。
- `target/issue-4103/*.txt`：场景摘要。
- `target/issue-4103/*-netstat.txt`：Windows 上按本次端口和测试 JVM PID 筛选的 TCP 快照。

非 Windows 环境仍检查服务端观察到的 socket 存活/EOF；不执行 Windows netstat 断言。
本次构建使用临时 Maven Central mirror 解决本机仓库来源/插件依赖问题；
临时 settings 位于 target，不是运行用例必需的项目配置。

## 实际执行路径

```text
RedirectingEurekaHttpClient.sendHeartBeat
  -> RestTemplateEurekaHttpClient.sendHeartBeat
  -> 本地 HTTP 端点返回 500，RestTemplate 抛出异常
  -> RedirectingEurekaHttpClient 清除旧 delegate 并调用 shutdown
  -> 4.1.0 的 RestTemplateEurekaHttpClient.shutdown 什么也不做
  -> 下一次心跳创建新的 RestTemplate / HttpClient / 连接池
```

本地端点在每轮先返回 200，确认心跳及持久连接可用，再返回一个完整的、
Content-Length 为 0 的 500 响应。异常路径由真实依赖执行，
不是在测试里手动模拟 Eureka 的异常处理或直接替换其源码。
最后服务端对旧连接发送 FIN，观察客户端是否主动关闭自己的 socket。

测试代理只记录 shutdown 次数；默认组完全转发真实实现。
对照组额外调用每个客户端独占的 `HttpComponentsClientHttpRequestFactory.destroy()`。
这证明生命周期关闭会改变连接残留结果，**不是已验证的业务修复方案或官方补丁回放**。
共享连接池、取消注册顺序等必须另行验证，不能将该实验代理直接用于生产。

## 结论边界

- 本用例直接使用实际 transport/decorator，未启动完整 Spring Boot ApplicationContext，
  也未运行 DiscoveryClient 的定时心跳线程；它证明当前版本组合存在这条缺陷路径，
  不证明你的任意运行服务一定选择了该 transport 或已发生泄漏。
- 正常返回的响应已经被消费；这里关注的是被丢弃的独立客户端/池未关闭，
  不能把结果描述成同一个连接池中 leased 连接必然持续增加。
- 测试保留 request factory 的强引用用于观测与最终清理，排除 GC 时机干扰。
  因此证明的是**缺少确定性的资源释放**，不用于声称生产中连接永远不会被 GC 回收。
- CLOSE_WAIT 计数在服务端发送 FIN 后取得；正常 keep-alive 本身不是泄漏。
- 用例故意断言 4.1.0 的行为。未来升级依赖后，缺陷断言失败可能表示行为已经改变，
  应结合实际版本、源码和观测更新断言，而不是让用例继续强行复现。
- 官方关联 [PR #4258](https://github.com/spring-cloud/spring-cloud-netflix/pull/4258)
  引入了连接清理相关改动；本实验没有升级依赖，也没有验证某个升级版本彻底修复所有生命周期场景。

## 调试位置

在 IDEA 中可对以下位置设置非挂起日志断点：

1. 本测试 `Probe.newClient`：观察独立客户端创建次数。
2. `RedirectingEurekaHttpClient.execute` 的异常处理：
   观察 delegate 被清空及 shutdown 被调用。
3. `RestTemplateEurekaHttpClient.shutdown`：4.1.0 中为空实现。
4. 本测试 `report`：查看 factories、shutdowns、peer.open，并对照 netstat 快照。

调试暂停过久可能触发 JUnit 超时；观察连接时优先使用非挂起日志断点。
