package jdktomcat.showcase.spring.cloud.gateway.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

/**
 * 启动类
 */
@EnableDiscoveryClient
@Slf4j
@SpringBootApplication
public class ApiApplication {

    public static void main(String[] args) {
        System.setProperty("reactor.ipc.netty.workerCount","256");
        System.setProperty("reactor.ipc.netty.selectCount","8");
        SpringApplication.run(ApiApplication.class);
    }
}
