package jdktomcat.showcase.skywalking.gateway.route.subscribe;

import cn.hutool.core.bean.BeanUtil;
import lombok.NonNull;
import org.reactivestreams.Subscription;
import org.slf4j.MDC;
import reactor.core.CoreSubscriber;
import reactor.util.context.Context;

import java.util.Optional;

public class MdcSubscriber<T> implements CoreSubscriber<T> {

    private static final String TRACE_ID = "traceId";

    private static final String SKYWALKING_CTX_SNAPSHOT = "SKYWALKING_CONTEXT_SNAPSHOT";

    private final CoreSubscriber<Object> actual;

    public MdcSubscriber(CoreSubscriber<Object> actual) {
        this.actual = actual;
    }

    @Override
    public void onSubscribe(@NonNull Subscription s) {
        actual.onSubscribe(s);
    }

    @Override
    public void onNext(Object o) {
        Context c = actual.currentContext();
        Optional<String> traceIdOptional = Optional.empty();
        if (!c.isEmpty() && c.hasKey(SKYWALKING_CTX_SNAPSHOT)) {
            traceIdOptional = Optional.of(c.get(SKYWALKING_CTX_SNAPSHOT)).map(BeanUtil::beanToMap)
                    .map(t -> t.get(TRACE_ID)).map(BeanUtil::beanToMap).map(t -> t.get("id")).map(Object::toString);
        }
        try (MDC.MDCCloseable ignored = MDC.putCloseable(TRACE_ID, traceIdOptional.orElse("N/A"))) {
            actual.onNext(o);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        actual.onError(throwable);
    }

    @Override
    public void onComplete() {
        actual.onComplete();
    }

    @Override
    public @NonNull Context currentContext() {
        return actual.currentContext();
    }
}
