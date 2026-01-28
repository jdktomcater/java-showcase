package com.jdktomcat.showcase.data.sharding.jdbc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * CommandLineRunner 基准压测配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "benchmark")
public class BenchmarkProperties {

    /**
     * 是否启用基准压测（启用后会在启动时执行并输出统计）
     */
    private boolean enabled = false;

    /**
     * 并发线程数
     */
    private int threads = 4;

    /**
     * 预热操作数（总数）
     */
    private long warmupOps = 10_000;

    /**
     * 正式压测操作数（总数）
     */
    private long ops = 100_000;

    /**
     * 用于生成主键的起始值（当 setIdExplicitly=true 时生效）
     */
    private long baseOrderIndex = 2_000_000_000_000_000_000L;

    /**
     * 是否显式设置主键 OrderIndex（false 时置空，依赖 mybatis-plus ASSIGN_ID 自动生成）
     */
    private boolean setIdExplicitly = false;

    /**
     * 冲突比例（0~1）。>0 时会按比例使用 fixedOrderIndex 作为主键，制造主键冲突（用于验证重试路径）
     * 注意：当 retry.interceptor.enabled=false 时，该场景大概率会出现大量失败，这是预期的。
     */
    private double conflictRate = 0.0d;

    /**
     * 固定主键值（用于制造主键冲突）
     */
    private long fixedOrderIndex = 1L;

    /**
     * 最多采样多少条延迟数据用于计算 P50/P95/P99（避免 ops 很大时占用过多内存）
     */
    private int maxSamples = 200_000;
}


