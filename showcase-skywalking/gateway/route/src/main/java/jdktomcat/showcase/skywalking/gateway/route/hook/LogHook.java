package jdktomcat.showcase.skywalking.gateway.route.hook;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jdktomcat.showcase.skywalking.gateway.route.subscribe.MdcSubscriber;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Operators;

@Component
public class LogHook {

    private static final String KEY = "logbackMdc";

    @PostConstruct
    public void setHook() {
        reactor.core.publisher.Hooks.onEachOperator(KEY, Operators.lift((scannable, coreSubscriber) -> new MdcSubscriber(coreSubscriber)));
    }

    @PreDestroy
    public void resetHook() {
        reactor.core.publisher.Hooks.resetOnEachOperator(KEY);
    }

}
