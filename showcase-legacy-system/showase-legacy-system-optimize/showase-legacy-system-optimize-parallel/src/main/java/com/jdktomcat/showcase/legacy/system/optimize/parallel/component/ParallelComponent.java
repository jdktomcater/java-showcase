package com.jdktomcat.showcase.legacy.system.optimize.parallel.component;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 通用并行/串行执行组件
 * <p>
 * 支持对列表元素执行转换逻辑，可选择串行或并行，结果顺序与输入一致。
 * </p>
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 串行：逐个查询用户
 * List<User> users = parallelComponent.executeSerial(userIds, userId -> userRepository.findById(userId));
 *
 * // 并行：并发调用 RPC
 * List<Order> orders = parallelComponent.executeParallel(orderIds, id -> orderRpcClient.getOrder(id));
 * }</pre>
 * </p>
 */
@Component
public class ParallelComponent {

    private final ExecutorService executor;

    public ParallelComponent(ExecutorService parallelExecutor) {
        this.executor = parallelExecutor;
    }

    /**
     * 串行执行：按输入顺序逐个处理
     *
     * @param items  输入列表
     * @param mapper 每个元素的处理逻辑
     * @return 处理结果列表，顺序与 items 一致
     */
    public <T, R> List<R> executeSerial(List<T> items, Function<T, R> mapper) {
        return items.stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    /**
     * 并行执行：使用线程池并发处理，结果顺序与输入一致
     *
     * @param items  输入列表
     * @param mapper 每个元素的处理逻辑（需线程安全）
     * @return 处理结果列表，顺序与 items 一致
     */
    public <T, R> List<R> executeParallel(List<T> items, Function<T, R> mapper) {
        List<CompletableFuture<R>> futures = items.stream()
                .map(item -> CompletableFuture.supplyAsync(() -> mapper.apply(item), executor))
                .collect(Collectors.toList());
        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }
}
