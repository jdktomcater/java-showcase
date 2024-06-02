package jdktomcat.showcase.springcloud.gateway.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

/**
 * 启动类
 */
@SpringBootApplication
public class ApiApplication {

    public static void main(String[] args) {
        System.setProperty("reactor.ipc.netty.workerCount","256");
        System.setProperty("reactor.ipc.netty.selectCount","8");
        SpringApplication.run(ApiApplication.class);
    }

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes().route(p -> p.path("/keep").uri("http://192.168.56.204:7001")).build();
    }
}
