package jdktomcat.showcase.mq.rocketmq.pressure.controller;

import jdktomcat.showcase.mq.rocketmq.pressure.tool.ConsumerGroupTestTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * RocketMQ消费组影响测试控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/test/rocketmq")
public class RocketMQTestController {

    @Autowired
    private ConsumerGroupTestTool testTool;

    private final ExecutorService testExecutor = Executors.newCachedThreadPool();

    /**
     * 执行基准测试
     */
    @PostMapping("/baseline")
    public TestResponse runBaselineTest(@RequestBody TestRequest request) {
        log.info("开始执行基准测试");

        CompletableFuture<TestResponse> future = CompletableFuture.supplyAsync(() -> {
            try {
                testTool.runConsumerGroupTest(10, 1000, 1800); // 10个消费组，1000消息/秒，30分钟
                return TestResponse.success("基准测试完成");
            } catch (Exception e) {
                log.error("基准测试失败", e);
                return TestResponse.error("基准测试失败: " + e.getMessage());
            }
        }, testExecutor);

        return TestResponse.success("基准测试已启动，请查看日志获取详细信息");
    }

    /**
     * 执行压力测试
     */
    @PostMapping("/stress")
    public TestResponse runStressTest(@RequestBody TestRequest request) {
        log.info("开始执行压力测试");
        CompletableFuture<TestResponse> future = CompletableFuture.supplyAsync(() -> {
            try {
                testTool.runConsumerGroupTest(50, 5000, 1800); // 50个消费组，5000消息/秒，30分钟
                return TestResponse.success("压力测试完成");
            } catch (Exception e) {
                log.error("压力测试失败", e);
                return TestResponse.error("压力测试失败: " + e.getMessage());
            }
        }, testExecutor);

        return TestResponse.success("压力测试已启动，请查看日志获取详细信息");
    }

    /**
     * 执行极限测试
     */
    @PostMapping("/limit")
    public TestResponse runLimitTest(@RequestBody TestRequest request) {
        log.info("开始执行极限测试");

        CompletableFuture<TestResponse> future = CompletableFuture.supplyAsync(() -> {
            try {
                testTool.runConsumerGroupTest(100, 10000, 1800); // 100个消费组，10000消息/秒，30分钟
                return TestResponse.success("极限测试完成");
            } catch (Exception e) {
                log.error("极限测试失败", e);
                return TestResponse.error("极限测试失败: " + e.getMessage());
            }
        }, testExecutor);

        return TestResponse.success("极限测试已启动，请查看日志获取详细信息");
    }

    /**
     * 执行自定义测试
     */
    @PostMapping("/custom")
    public TestResponse runCustomTest(@RequestBody CustomTestRequest request) {
        log.info("开始执行自定义测试 - 消费组数量: {}, 消息速率: {}, 持续时间: {}",
                request.getConsumerGroupCount(), request.getMessageRate(), request.getDurationSeconds());

        CompletableFuture<TestResponse> future = CompletableFuture.supplyAsync(() -> {
            try {
                testTool.runConsumerGroupTest(
                        request.getConsumerGroupCount(),
                        request.getMessageRate(),
                        request.getDurationSeconds()
                );
                return TestResponse.success("自定义测试完成");
            } catch (Exception e) {
                log.error("自定义测试失败", e);
                return TestResponse.error("自定义测试失败: " + e.getMessage());
            }
        }, testExecutor);

        return TestResponse.success("自定义测试已启动，请查看日志获取详细信息");
    }

    /**
     * 获取测试统计信息
     */
    @GetMapping("/statistics")
    public TestResponse getStatistics() {
        try {
            ConsumerGroupTestTool.TestStatistics stats = testTool.getTestStatistics();
            return TestResponse.success("获取统计信息成功", stats);
        } catch (Exception e) {
            log.error("获取统计信息失败", e);
            return TestResponse.error("获取统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public TestResponse health() {
        return TestResponse.success("RocketMQ测试服务运行正常");
    }

    /**
     * 测试请求
     */
    public static class TestRequest {
        // 基础测试请求，可以根据需要扩展
    }

    /**
     * 自定义测试请求
     */
    public static class CustomTestRequest {
        private int consumerGroupCount = 10;
        private int messageRate = 1000;
        private int durationSeconds = 1800;

        // Getters and Setters
        public int getConsumerGroupCount() { return consumerGroupCount; }
        public void setConsumerGroupCount(int consumerGroupCount) { this.consumerGroupCount = consumerGroupCount; }

        public int getMessageRate() { return messageRate; }
        public void setMessageRate(int messageRate) { this.messageRate = messageRate; }

        public int getDurationSeconds() { return durationSeconds; }
        public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }
    }

    /**
     * 测试响应
     */
    public static class TestResponse {
        private boolean success;
        private String message;
        private Object data;

        public TestResponse(boolean success, String message, Object data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        public static TestResponse success(String message) {
            return new TestResponse(true, message, null);
        }

        public static TestResponse success(String message, Object data) {
            return new TestResponse(true, message, data);
        }

        public static TestResponse error(String message) {
            return new TestResponse(false, message, null);
        }

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
    }
}