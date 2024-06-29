package jdktomcat.showcase.skywalking.gateway.route;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import reactor.netty.ReactorNetty;

/**
 * 启动类
 */
@SpringBootApplication
public class RouteApplication {

    public static void main(String[] args) {
        System.setProperty(ReactorNetty.IO_SELECT_COUNT, "4");
        System.setProperty(ReactorNetty.IO_WORKER_COUNT, "32");
        SpringApplication.run(RouteApplication.class);
    }

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes().route(p -> p.path("/keep").uri("http://192.168.56.204:7001")).build();
    }
}
