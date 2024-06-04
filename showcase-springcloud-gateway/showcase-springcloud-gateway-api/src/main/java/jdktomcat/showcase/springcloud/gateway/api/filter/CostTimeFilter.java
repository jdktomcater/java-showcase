package jdktomcat.showcase.springcloud.gateway.api.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RefreshScope
public class CostTimeFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long start = System.currentTimeMillis();
        Mono<Void> mon = chain.filter(exchange);
        long end = System.currentTimeMillis();
        log.info("cost time : {} ms", end - start);
        return mon;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
