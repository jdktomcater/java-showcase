# showcase-spring-cloud

## Docker 镜像（Maven 插件：Jib）

本项目已在父 `pom.xml` 里统一配置 `com.google.cloud.tools:jib-maven-plugin`。

### 构建本地 Docker 镜像（需要本机已安装并启动 Docker）

```bash
cd /Users/yuhata/workspace/github/jdktomcater/java-showcase/showcase-spring-cloud

# Eureka Server (8761)
mvn -pl showcase-spring-cloud-eureka/showcase-spring-cloud-eureka-server -DskipTests package jib:dockerBuild

# Gateway API (7000)
mvn -pl showcase-spring-cloud-gateway/showcase-spring-cloud-gateway-api -DskipTests package jib:dockerBuild

# Gateway Service (7001)
mvn -pl showcase-spring-cloud-gateway/showcase-spring-cloud-gateway-service -DskipTests package jib:dockerBuild
```

默认镜像名：

- `showcase/<artifactId>:<version>`

可通过参数覆盖镜像前缀：

```bash
mvn -pl showcase-spring-cloud-eureka/showcase-spring-cloud-eureka-server -Ddocker.image.prefix=myrepo -DskipTests package jib:dockerBuild
```

