package com.jdktomcat.showcase.limiter.sentinel.handler;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 全局 Web 层 Sentinel 异常处理器。
 *
 * <p>当 HTTP 入口（Web 自动埋点）被 Sentinel 拦截时，框架回调此 Bean 而不是
 * 抛出异常，因此 Spring {@code @ControllerAdvice} 无法捕获 Web 层的 BlockException。
 *
 * <p>注意：{@link com.alibaba.csp.sentinel.annotation.SentinelResource} 注解资源
 * 的限流由 {@code blockHandler} 属性处理，不经过本类。
 */
@Component
public class SentinelBlockExceptionHandler implements BlockExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(SentinelBlockExceptionHandler.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       BlockException ex) throws Exception {

        String resource = ex.getRule() != null ? ex.getRule().getResource() : "unknown";
        log.warn("Sentinel blocked uri={} resource={} type={}",
                request.getRequestURI(), resource, ex.getClass().getSimpleName());

        String msg;
        if (ex instanceof FlowException) {
            msg = "请求过于频繁，请稍后重试 (flow)";
        } else if (ex instanceof DegradeException) {
            msg = "服务降级中，请稍后重试 (degrade)";
        } else if (ex instanceof ParamFlowException) {
            msg = "热点参数限流，请稍后重试 (param)";
        } else if (ex instanceof SystemBlockException) {
            msg = "系统负载过高，请稍后重试 (system)";
        } else if (ex instanceof AuthorityException) {
            msg = "访问被拒绝 (authority)";
        } else {
            msg = "请求被限流，请稍后重试";
        }

        response.setStatus(429);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), Map.of("code", 429, "msg", msg));
    }
}
