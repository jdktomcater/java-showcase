package com.jdktomcat.showcase.legacy.system.optimize.parallel.component;

import com.jdktomcat.showcase.legacy.system.optimize.parallel.config.ParallelConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.skywalking.apm.toolkit.trace.CallableWrapper;
import org.apache.skywalking.apm.toolkit.trace.SupplierWrapper;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiFunction;
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
@Slf4j
@Component
public class ParallelComponent {

    private final ExecutorService executor;

    private final ParallelConfig parallelConfig;

    public ParallelComponent(ExecutorService parallelExecutor, ParallelConfig parallelConfig) {
        this.executor = parallelExecutor;
        this.parallelConfig = parallelConfig;
    }

    /**
     * 串行执行：按输入顺序逐个处理
     *
     * @param items  输入列表
     * @param mapper 每个元素的处理逻辑，返回 null 时该结果会被过滤
     * @return 处理结果列表，顺序与 items 一致，已过滤 null
     */
    public <T, R> List<R> executeSerial(List<T> items, Function<T, R> mapper) {
        log.info("password:{}", parallelConfig.getPassword());
        log.info("password:{}", parallelConfig.getPassword());
        return items.stream().map(mapper).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 并行执行：使用线程池并发处理，结果顺序与输入一致
     *
     * @param items  输入列表
     * @param mapper 每个元素的处理逻辑（需线程安全），返回 null 时该结果会被过滤
     * @return 处理结果列表，顺序与 items 一致，已过滤 null
     */
    public <T, R> List<R> executeParallel(List<T> items, Function<T, R> mapper) {
        List<CompletableFuture<R>> futures = items.stream().map(item -> CompletableFuture.supplyAsync(SupplierWrapper.of(() -> mapper.apply(item)), executor)).collect(Collectors.toList());
        return futures.stream().map(CompletableFuture::join).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 并行执行：使用线程池并发处理，结果顺序与输入一致。异常时由 exceptionMapper 处理并返回兜底结果。
     *
     * @param items           输入列表
     * @param mapper          每个元素的处理逻辑（需线程安全）
     * @param exceptionMapper 异常时的处理逻辑，入参为 (item, exception)，返回兜底结果（可返回 null 表示跳过）
     * @return 处理结果列表，顺序与 items 一致
     */
    public <T, R> List<R> executeParallel(List<T> items, Function<T, R> mapper, BiFunction<T, Exception, R> exceptionMapper) {
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        List<Triple<Integer, T, Future<R>>> futures = new ArrayList<>(items.size());
        for (int i = 0; i < items.size(); i++) {
            final T it = items.get(i);
            futures.add(Triple.of(i, it, executor.submit(new CallableWrapper<>(() -> mapper.apply(it)))));
        }
        List<Pair<Integer, R>> results = new ArrayList<>(items.size());
        long timeoutMs = parallelConfig.getTaskTimeout();
        futures.stream().parallel().forEach(result -> {
            T item = result.getMiddle();
            Future<R> future = result.getRight();
            R res;
            try {
                res = future.get(timeoutMs, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                log.error("ParallelComponent task timeout or cancelled! item={}", item, e);
                res = exceptionMapper.apply(item, e);
            }
            if (res != null) {
                results.add(Pair.of(result.getLeft(), res));
            }
        });
        log.info("password:{}", parallelConfig.getPassword());
        log.info("password:{}", parallelConfig.getPassword());
        // 保证原先顺序
        results.sort(Comparator.comparing(Pair::getLeft));
        return results.stream().map(Pair::getRight).collect(Collectors.toList());
    }


    @PreDestroy
    public void shutdown() {
        executor.shutdown();
    }
}
