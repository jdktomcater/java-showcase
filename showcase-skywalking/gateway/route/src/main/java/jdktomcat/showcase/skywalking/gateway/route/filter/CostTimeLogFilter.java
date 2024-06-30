package jdktomcat.showcase.skywalking.gateway.route.filter;

import jdktomcat.showcase.skywalking.gateway.route.util.SkywalkingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author Timmy
 * @version V1.0
 * @description 类描述：traceId拦截器
 * @date 2024/1/8
 */
@Component
@Slf4j
public class CostTimeLogFilter implements GlobalFilter, Ordered {

    private static final String START_TIME = "startTime";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        SkywalkingUtil.putTidIntoMdc(exchange);
        exchange.getAttributes().put(START_TIME, System.currentTimeMillis());
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            Long startTime = exchange.getAttribute(START_TIME);
            if (startTime != null) {
                log.info("{} cost: {}ms",exchange.getRequest().getURI().getRawPath(),(System.currentTimeMillis() - startTime));
                SkywalkingUtil.putTidIntoMdc(exchange);
            }
        }));
    }

    /**
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     *
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}
