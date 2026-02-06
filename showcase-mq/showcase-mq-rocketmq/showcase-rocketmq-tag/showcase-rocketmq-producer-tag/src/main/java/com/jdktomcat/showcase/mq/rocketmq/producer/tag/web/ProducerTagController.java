package com.jdktomcat.showcase.mq.rocketmq.producer.tag.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RocketMQ消费组影响测试控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/rocketmq/producer/tag")
public class ProducerTagController {

    // /**
    //  * 执行压力测试
    //  */
    // @PostMapping("/stress")
    // public TestResponse runStressTest(@RequestBody TestRequest request) {
    //     log.info("开始执行压力测试");
    //     CompletableFuture<TestResponse> future = CompletableFuture.supplyAsync(() -> {
    //         try {
    //             testTool.runConsumerGroupTest(50, 5000, 1800); // 50个消费组，5000消息/秒，30分钟
    //             return TestResponse.success("压力测试完成");
    //         } catch (Exception e) {
    //             log.error("压力测试失败", e);
    //             return TestResponse.error("压力测试失败: " + e.getMessage());
    //         }
    //     }, testExecutor);

    //     return TestResponse.success("压力测试已启动，请查看日志获取详细信息");
    // }

}
