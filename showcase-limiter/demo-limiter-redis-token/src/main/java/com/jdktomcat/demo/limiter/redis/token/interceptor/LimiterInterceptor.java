package com.jdktomcat.demo.limiter.redis.token.interceptor;

import com.jdktomcat.demo.limiter.redis.token.annotation.Limiter;
import com.jdktomcat.demo.limiter.redis.token.component.RedisComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

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
public class LimiterInterceptor extends HandlerInterceptorAdapter {

    /**
     * redis工具类
     */
    @Autowired
    private RedisComponent redisComponent;

    /**
     * 特征值前缀
     */
    private static final String EIGENVALUE_PRE = "typay:general:eigenvalue:";

    /**
     * 限流拦截实现(滑动窗口)
     *
     * @param request  请求
     * @param response 响应
     * @param handler 处理器
     * @return true:放行 false:拦截
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            Limiter annotation = method.getAnnotation(Limiter.class);
            if (annotation != null) {
                String uri = request.getRequestURI();
                int window = annotation.window();
                switch (annotation.unit()){
                    case SECONDS:
                        break;
                    case MINUTES:
                        window = window * 60;
                        break;
                    case HOURS:
                        window = window * 60 * 60;
                        break;
                    case DAYS:
                        window = window * 60 * 60 * 24;
                        break;
                    case MILLISECONDS:
                        window = window / 1000;
                        break;
                    default:
                        break;
                }
                return redisComponent.limitFlowFixedWindow(EIGENVALUE_PRE + uri + ":" +request.getRemoteAddr(), annotation.value(), window);
            }
            return true;
        }
        return super.preHandle(request, response, handler);
    }
}
