package com.jdktomcat.showcase.limiter.redis.token.interceptor;

import com.jdktomcat.showcase.limiter.redis.token.annotation.Limiter;
import com.jdktomcat.showcase.limiter.redis.token.component.RedisComponent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

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
        AsyncHandlerInterceptor.super.afterConcurrentHandlingStarted(request, response, handler);
    }

    /**
     * Interception point after successful execution of a handler.
     * Called after HandlerAdapter actually invoked the handler, but before the
     * DispatcherServlet renders the view. Can expose additional model objects
     * to the view via the given ModelAndView.
     * <p>DispatcherServlet processes a handler in an execution chain, consisting
     * of any number of interceptors, with the handler itself at the end.
     * With this method, each interceptor can post-process an execution,
     * getting applied in inverse order of the execution chain.
     * <p><strong>Note:</strong> special considerations apply for asynchronous
     * request processing. For more details see
     * {@link AsyncHandlerInterceptor}.
     * <p>The default implementation is empty.
     *
     * @param request      current HTTP request
     * @param response     current HTTP response
     * @param handler      the handler (or {@link HandlerMethod}) that started asynchronous
     *                     execution, for type and/or instance examination
     * @param modelAndView the {@code ModelAndView} that the handler returned
     *                     (can also be {@code null})
     * @throws Exception in case of errors
     */
    @Override
    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        AsyncHandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
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
    }
}
