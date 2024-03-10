package jdktomcat.demo.skywalking.gateway.route;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

/**
 * 启动类
 */
@SpringBootApplication
public class SkywalkingGatewayRouteApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkywalkingGatewayRouteApplication.class);
    }

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p.path("/reset").filters(f -> f.addRequestHeader("Hello", "World")).uri("http://192.168.56.1:7001"))
                .route(p -> p.path("/keep").filters(f -> f.addRequestHeader("Hello", "World")).uri("http://192.168.56.1:7001"))
                .build();
    }

}
