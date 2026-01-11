package com.jdktomcat.showcase.limiter.redis.token.interceptor;

import com.jdktomcat.showcase.limiter.redis.token.annotation.Limiter;
import com.jdktomcat.showcase.limiter.redis.token.component.RedisComponent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author ZF-Timmy
 * @version V1.0
 * @description 类描述：限流拦截器
 * @date 2023/11/9
 */
@Slf4j
@Component
public class LimiterInterceptor implements AsyncHandlerInterceptor {

    /**
     * redis tool component
     */
    @Autowired
    private RedisComponent redisComponent;

    /**
     * prefix
     */
    private static final String EIGENVALUE_PRE = "eigenvalue:";

    /**
     * thread hold permit id
     */
    public static final ThreadLocal<String> SEMAPHORE_HOLDER = new ThreadLocal<>();

    /**
     * 限流拦截实现(滑动窗口)
     *
     * @param request  请求
     * @param response 响应
     * @param handler 处理器
     * @return true:放行 false:拦截
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            Method method = handlerMethod.getMethod();
            Limiter annotation = method.getAnnotation(Limiter.class);
            if (annotation != null) {
                String key = EIGENVALUE_PRE + request.getRequestURI() + ":" +request.getRemoteAddr();
                String permitId = redisComponent.getSemaphorePermit(key, annotation.lease(), annotation.token());
                if(!StringUtils.hasLength(permitId)){
                    log.warn("business key：{} over request", key);
                    return false;
                }
                SEMAPHORE_HOLDER.set(permitId);
                return true;
            }
            return true;
        }
        return true;
    }

    /**
     * Called instead of {@code postHandle} and {@code afterCompletion}
     * when the handler is being executed concurrently.
     * <p>Implementations may use the provided request and response but should
     * avoid modifying them in ways that would conflict with the concurrent
     * execution of the handler. A typical use of this method would be to
     * clean up thread-local variables.
     *
     * @param request  the current request
     * @param response the current response
     * @param handler  the handler (or {@link HandlerMethod}) that started async
     *                 execution, for type and/or instance examination
     * @throws Exception in case of errors
     */
    @Override
    public void afterConcurrentHandlingStarted(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            Method method = handlerMethod.getMethod();
            Limiter annotation = method.getAnnotation(Limiter.class);
            if (annotation != null) {
                String key = EIGENVALUE_PRE + request.getRequestURI() + ":" +request.getRemoteAddr();
                String permitId = SEMAPHORE_HOLDER.get();
                SEMAPHORE_HOLDER.remove();
                boolean release = redisComponent.releaseSemaphore(key, permitId);
                log.warn("business key：{} permit:{} release result:{}", key, permitId, release);
            }
        }
        AsyncHandlerInterceptor.super.afterConcurrentHandlingStarted(request, response, handler);
    }
}
