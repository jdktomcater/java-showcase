package com.jdktomcat.showcase.common.threadpool.controller;

import com.jdktomcat.showcase.common.threadpool.response.AjaxResult;
import com.jdktomcat.showcase.common.threadpool.service.ThreadPoolService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping(value = "/tp")
public class ThreadPoolController {

    @Autowired
    private ThreadPoolService threadPoolService;

    @Resource(name = "remoteRequestParallelExecutor")
    private ThreadPoolTaskExecutor remoteRequestParallelExecutor;

    @GetMapping("/say")
    public String say(String name) {
        threadPoolService.say();
        return "hello" + name;
    }

    /**
     * 取消订单
     */
    @PostMapping("/cancelOrder")
    @ResponseBody
    public AjaxResult cancelOrder(@RequestBody List<Long> thirdWithdrawOrderList) {
        if (thirdWithdrawOrderList == null || thirdWithdrawOrderList.isEmpty()) {
            return AjaxResult.error("至少选择一条数据");
        }
        Map<Long, Integer> orderMap = new HashMap<>(thirdWithdrawOrderList.size());
        for (Long order : thirdWithdrawOrderList) {
            orderMap.put(order, order.intValue());
        }
        long startTime = System.currentTimeMillis();
        List<Future<Integer>> futures = new ArrayList<>();
        for (Long thirdWithdrawOrder : thirdWithdrawOrderList) {
            Callable<Integer> task = () -> {
                try {
                    Integer value = orderMap.get(thirdWithdrawOrder);
                    Thread.sleep(1000);
                    return value == null ? 0 : value;
                } catch (Exception e) {
                    return 0;
                }
            };
            futures.add(remoteRequestParallelExecutor.submit(task));
        }
        // 单笔任务最大等待时间，避免下游卡住时主线程无限阻塞
        final long getTimeoutSeconds = 60;
        List<Integer> resultList = new ArrayList<>();
        for (Future<Integer> future : futures) {
            try {
                resultList.add(future.get(getTimeoutSeconds, TimeUnit.SECONDS));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        long cost = System.currentTimeMillis() - startTime;
        log.info("批量取消订单完成，本次请求共处理:{} 条，耗时:{} ms", futures.size(), cost);
        return AjaxResult.success(resultList);
    }
}
