package jdktomcat.showcase.spring.cloud.gateway.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 启动类
 */
@EnableDiscoveryClient
@Slf4j
@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
//        System.setProperty("reactor.ipc.netty.workerCount","256");
//        System.setProperty("reactor.ipc.netty.selectCount","8");
        SpringApplication.run(GatewayApplication.class);
    }
}
