package jdktomcat.demo.skywalking.gateway.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

/**
 * 启动类
 */
@SpringBootApplication
public class SkywalkingGatewayApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkywalkingGatewayApiApplication.class);
    }

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes().route(p -> p.path("/health/check").uri("http://172.31.184.87:8082")).build();
    }
}
