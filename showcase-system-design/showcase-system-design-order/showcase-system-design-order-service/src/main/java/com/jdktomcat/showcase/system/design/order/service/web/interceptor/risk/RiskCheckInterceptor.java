package com.jdktomcat.showcase.system.design.order.service.web.interceptor.risk;

import com.jdktomcat.showcase.system.design.order.service.feign.RiskFeignClient;
import com.jdktomcat.showcase.system.design.risk.client.dto.RiskCheckRequest;
import com.jdktomcat.showcase.system.design.risk.client.dto.RiskCheckResponse;
import com.jdktomcat.showcase.system.design.risk.client.enums.RiskLevel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/**
 * 风控检查拦截器：仅对打了 {@link Risk} 注解的处理器方法发起风控检查，
 * 风控结果为 BLOCK 时直接拒绝请求（返回 403）。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RiskCheckInterceptor implements HandlerInterceptor {

    private final RiskFeignClient riskFeignClient;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        Risk risk = handlerMethod.getMethodAnnotation(Risk.class);
        if (risk == null) {
            risk = handlerMethod.getBeanType().getAnnotation(Risk.class);
        }
        if (risk == null) {
            return true;
        }

        RiskCheckRequest riskRequest = buildRiskCheckRequest(request);
        try {
            ResponseEntity<RiskCheckResponse> result = riskFeignClient.check(riskRequest);
            RiskCheckResponse riskResponse = result.getBody();
            if (riskResponse != null && RiskLevel.BLOCK == riskResponse.getRiskLevel()) {
                log.warn("风控拦截: requestId={}, userId={}, score={}, reason={}",
                        riskRequest.getRequestId(), riskRequest.getUserId(),
                        riskResponse.getRiskScore(), riskResponse.getReason());
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":403,\"message\":\"Request blocked by risk control\"}");
                return false;
            }
        } catch (Exception e) {
            log.error("风控检查异常，放行请求: requestId={}", riskRequest.getRequestId(), e);
        }
        return true;
    }

    private RiskCheckRequest buildRiskCheckRequest(HttpServletRequest request) {
        RiskCheckRequest riskRequest = new RiskCheckRequest();
        riskRequest.setRequestId(UUID.randomUUID().toString());
        riskRequest.setUserIp(getClientIp(request));
        riskRequest.setEventType(request.getRequestURI());
        return riskRequest;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // X-Forwarded-For 可能包含多个 IP，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
