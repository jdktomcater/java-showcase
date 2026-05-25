package com.jdktomcat.showcase.limiter.sentinel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Sentinel 限流 / 熔断 / 热点参数限流 示例。
 *
 * <p>启动后访问：
 * <pre>
 *   流控（QPS）     GET  http://localhost:8090/order/query?id=1
 *   熔断降级        GET  http://localhost:8090/order/pay?id=1
 *   热点参数限流    GET  http://localhost:8090/order/hot?itemId=100
 *   系统自适应限流  GET  http://localhost:8090/order/system
 * </pre>
 *
 * <p>Sentinel Dashboard 下载：
 * <a href="https://github.com/alibaba/Sentinel/releases">Sentinel Releases</a>
 * <br>启动：java -jar sentinel-dashboard-1.8.6.jar --server.port=8080
 */
@SpringBootApplication
public class SentinelApplication {

    public static void main(String[] args) {
        SpringApplication.run(SentinelApplication.class, args);
    }
}
