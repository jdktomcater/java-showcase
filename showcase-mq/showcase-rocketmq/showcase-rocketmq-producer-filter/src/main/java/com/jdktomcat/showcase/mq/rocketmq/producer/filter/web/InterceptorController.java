package com.jdktomcat.showcase.mq.rocketmq.producer.filter.web;

import com.jdktomcat.showcase.mq.rocketmq.producer.filter.component.MessageProducerComponent;
import com.jdktomcat.showcase.mq.rocketmq.producer.filter.config.InterceptorConfig;
import com.jdktomcat.showcase.mq.rocketmq.producer.filter.interceptor.MessageInterceptor;
import com.jdktomcat.showcase.mq.rocketmq.producer.filter.interceptor.MessageStatisticsInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 拦截器管理控制器
 * 
 * @author ZF-Timmy
 * @version V1.0
 * @date 2024/01/01
 */
@Slf4j
@RestController
@RequestMapping("/api/rocketmq/interceptor")
public class InterceptorController {
    
    @Autowired
    private MessageProducerComponent messageProducerComponent;
    
    @Autowired
    private InterceptorConfig interceptorConfig;
    
    /**
     * 获取拦截器状态信息
     */
    @GetMapping("/status")
    public Map<String, Object> getInterceptorStatus() {
        Map<String, Object> status = new HashMap<>();
        
        try {
            // 获取拦截器链信息
            var interceptorChain = messageProducerComponent.getInterceptorChain();
            status.put("interceptorCount", interceptorChain.getInterceptorCount());
            status.put("interceptors", interceptorChain.getInterceptors());
            
            // 获取配置信息
            status.put("validationEnabled", interceptorConfig.isValidationEnabled());
            status.put("encryptionEnabled", interceptorConfig.isEncryptionEnabled());
            status.put("tagFilterEnabled", interceptorConfig.isTagFilterEnabled());
            status.put("statisticsEnabled", interceptorConfig.isStatisticsEnabled());
            
            // 获取统计信息
            MessageStatisticsInterceptor statisticsInterceptor = 
                    interceptorChain.getInterceptor(MessageStatisticsInterceptor.class);
            if (statisticsInterceptor != null) {
                status.put("totalMessageCount", statisticsInterceptor.getTotalMessageCount());
                status.put("successMessageCount", statisticsInterceptor.getSuccessMessageCount());
                status.put("failedMessageCount", statisticsInterceptor.getFailedMessageCount());
                status.put("averageSendTime", statisticsInterceptor.getAverageSendTime());
            }
            
            status.put("success", true);
            status.put("message", "获取拦截器状态成功");
            
        } catch (Exception e) {
            log.error("获取拦截器状态失败", e);
            status.put("success", false);
            status.put("message", "获取拦截器状态失败: " + e.getMessage());
        }
        
        return status;
    }
    
    /**
     * 获取详细统计信息
     */
    @GetMapping("/statistics")
    public Map<String, Object> getStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            var interceptorChain = messageProducerComponent.getInterceptorChain();
            MessageStatisticsInterceptor statisticsInterceptor = 
                    interceptorChain.getInterceptor(MessageStatisticsInterceptor.class);
            
            if (statisticsInterceptor != null) {
                result.put("statistics", statisticsInterceptor.getStatistics());
                result.put("success", true);
                result.put("message", "获取统计信息成功");
            } else {
                result.put("success", false);
                result.put("message", "统计拦截器未找到");
            }
            
        } catch (Exception e) {
            log.error("获取统计信息失败", e);
            result.put("success", false);
            result.put("message", "获取统计信息失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 重置统计信息
     */
    @PostMapping("/statistics/reset")
    public Map<String, Object> resetStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            var interceptorChain = messageProducerComponent.getInterceptorChain();
            MessageStatisticsInterceptor statisticsInterceptor = 
                    interceptorChain.getInterceptor(MessageStatisticsInterceptor.class);
            
            if (statisticsInterceptor != null) {
                statisticsInterceptor.resetStatistics();
                result.put("success", true);
                result.put("message", "统计信息重置成功");
            } else {
                result.put("success", false);
                result.put("message", "统计拦截器未找到");
            }
            
        } catch (Exception e) {
            log.error("重置统计信息失败", e);
            result.put("success", false);
            result.put("message", "重置统计信息失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 获取拦截器配置
     */
    @GetMapping("/config")
    public Map<String, Object> getInterceptorConfig() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            result.put("config", interceptorConfig);
            result.put("success", true);
            result.put("message", "获取配置信息成功");
            
        } catch (Exception e) {
            log.error("获取配置信息失败", e);
            result.put("success", false);
            result.put("message", "获取配置信息失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 获取所有拦截器列表
     */
    @GetMapping("/list")
    public Map<String, Object> getInterceptorList() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            var interceptorChain = messageProducerComponent.getInterceptorChain();
            List<MessageInterceptor> interceptors = interceptorChain.getInterceptors();
            
            Map<String, Object> interceptorInfo = new HashMap<>();
            for (MessageInterceptor interceptor : interceptors) {
                String className = interceptor.getClass().getSimpleName();
                interceptorInfo.put(className, Map.of(
                    "order", interceptor.getOrder(),
                    "enabled", true,
                    "type", interceptor.getClass().getName()
                ));
            }
            
            result.put("interceptors", interceptorInfo);
            result.put("success", true);
            result.put("message", "获取拦截器列表成功");
            
        } catch (Exception e) {
            log.error("获取拦截器列表失败", e);
            result.put("success", false);
            result.put("message", "获取拦截器列表失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            var interceptorChain = messageProducerComponent.getInterceptorChain();
            
            result.put("status", "UP");
            result.put("interceptorCount", interceptorChain.getInterceptorCount());
            result.put("timestamp", System.currentTimeMillis());
            result.put("success", true);
            
        } catch (Exception e) {
            log.error("健康检查失败", e);
            result.put("status", "DOWN");
            result.put("error", e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }
}
