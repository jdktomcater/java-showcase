package com.jdktomcat.showcase.limiter.sentinel.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.jdktomcat.showcase.limiter.sentinel.config.SentinelRuleConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 演示 Sentinel 四种限流/熔断场景。
 *
 * <pre>
 * 场景 1 - 流控 (QPS)        GET /order/query?id=1
 *   快速连续请求 > 2 QPS → 触发 blockHandler，返回限流提示
 *
 * 场景 2 - 熔断降级 (慢调用) GET /order/pay?id=1&slow=true
 *   slow=true 时接口睡眠 800ms，多次请求后触发熔断，返回 fallback
 *
 * 场景 3 - 热点参数限流       GET /order/hot?itemId=1
 *   itemId=1 默认 1 QPS；itemId=100 特例 5 QPS
 *
 * 场景 4 - Web 层自动埋点     GET /order/system
 *   Sentinel 自动对所有 HTTP 入口埋点，无需 @SentinelResource
 * </pre>
 */
@RestController
@RequestMapping("/order")
public class DemoController {

    private static final Logger log = LoggerFactory.getLogger(DemoController.class);

    // ── 场景 1：QPS 流控 ──────────────────────────────────────────────────

    /**
     * blockHandler 在当前类中定义，签名需与原方法一致并追加 BlockException 参数。
     * fallback 处理业务异常（非限流）。
     */
    @GetMapping("/query")
    @SentinelResource(
            value        = SentinelRuleConfig.RESOURCE_QUERY,
            blockHandler = "queryBlock",
            fallback     = "queryFallback"
    )
    public Map<String, Object> query(@RequestParam Long id) {
        log.debug("query id={}", id);
        return Map.of("code", 0, "data", "order#" + id);
    }

    public Map<String, Object> queryBlock(Long id, BlockException ex) {
        log.warn("query blocked id={} rule={}", id, ex.getClass().getSimpleName());
        return Map.of("code", 429, "msg", "请求过于频繁，请稍后重试");
    }

    public Map<String, Object> queryFallback(Long id, Throwable t) {
        log.error("query fallback id={}", id, t);
        return Map.of("code", 500, "msg", "服务异常，请稍后重试");
    }

    // ── 场景 2：慢调用熔断 ────────────────────────────────────────────────

    @GetMapping("/pay")
    @SentinelResource(
            value        = SentinelRuleConfig.RESOURCE_PAY,
            blockHandler = "payBlock",
            fallback     = "payFallback"
    )
    public Map<String, Object> pay(@RequestParam Long id,
                                   @RequestParam(defaultValue = "false") boolean slow) {
        if (slow) {
            try {
                TimeUnit.MILLISECONDS.sleep(800);  // 模拟慢调用 800ms > 阈值 500ms
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        log.debug("pay id={}", id);
        return Map.of("code", 0, "data", "paid#" + id);
    }

    public Map<String, Object> payBlock(Long id, boolean slow, BlockException ex) {
        log.warn("pay blocked id={} type={}", id, ex.getClass().getSimpleName());
        return Map.of("code", 429, "msg", "支付服务繁忙（熔断中），请稍后重试");
    }

    public Map<String, Object> payFallback(Long id, boolean slow, Throwable t) {
        log.error("pay fallback id={}", id, t);
        return Map.of("code", 500, "msg", "支付异常，请联系客服");
    }

    // ── 场景 3：热点参数限流 ──────────────────────────────────────────────

    /**
     * 热点限流必须使用 @SentinelResource；参数索引从 0 开始对应方法第一个参数。
     * itemId=100 享有特例阈值 5 QPS，其他 itemId 默认 1 QPS。
     */
    @GetMapping("/hot")
    @SentinelResource(
            value        = SentinelRuleConfig.RESOURCE_HOT,
            blockHandler = "hotBlock"
    )
    public Map<String, Object> hot(@RequestParam String itemId) {
        log.debug("hot itemId={}", itemId);
        return Map.of("code", 0, "data", "item#" + itemId);
    }

    public Map<String, Object> hotBlock(String itemId, BlockException ex) {
        log.warn("hot blocked itemId={}", itemId);
        return Map.of("code", 429, "msg", "商品 " + itemId + " 访问过热，请稍后");
    }

    // ── 场景 4：系统自适应 / Web 自动埋点 ────────────────────────────────

    /**
     * 无需 @SentinelResource；spring-cloud-starter-alibaba-sentinel 自动为所有
     * HTTP 入口创建名为 "GET:/order/system" 的资源，系统规则生效后会被限流。
     */
    @GetMapping("/system")
    public Map<String, Object> system() {
        return Map.of("code", 0, "msg", "系统正常");
    }
}
