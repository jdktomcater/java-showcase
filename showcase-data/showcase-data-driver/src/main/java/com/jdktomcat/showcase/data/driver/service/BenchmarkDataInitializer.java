package com.jdktomcat.showcase.data.driver.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.jdktomcat.showcase.data.driver.config.BenchmarkProperties;
import com.jdktomcat.showcase.data.driver.domain.DriverOrder;
import com.jdktomcat.showcase.data.driver.mapper.DriverBenchmarkMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BenchmarkDataInitializer {

    public static final String[] STATUSES = {"INIT", "PAID", "CLOSED", "REFUND"};

    public static final LocalDateTime BASE_CREATED_AT = LocalDateTime.of(2024, 1, 1, 0, 0, 0);

    private final DriverBenchmarkMapper driverBenchmarkMapper;
    private final BenchmarkProperties benchmarkProperties;

    @Transactional(rollbackFor = Exception.class)
    public void initialize() {
        long rowCount = driverBenchmarkMapper.countAll();
        if (rowCount == benchmarkProperties.getSeedSize()) {
            log.info("Benchmark seed data already exists, rows={}", rowCount);
            return;
        }

        driverBenchmarkMapper.deleteAll();
        List<DriverOrder> batch = new ArrayList<DriverOrder>(benchmarkProperties.getBatchSize());
        for (int i = 1; i <= benchmarkProperties.getSeedSize(); i++) {
            batch.add(buildOrder(i));
            if (batch.size() == benchmarkProperties.getBatchSize()) {
                driverBenchmarkMapper.batchInsert(batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            driverBenchmarkMapper.batchInsert(batch);
        }
        log.info("Benchmark seed data initialized, rows={}", benchmarkProperties.getSeedSize());
    }

    private DriverOrder buildOrder(int index) {
        DriverOrder driverOrder = new DriverOrder();
        driverOrder.setId((long) index);
        driverOrder.setUserName("user_" + index);
        driverOrder.setStatus(STATUSES[index % STATUSES.length]);
        driverOrder.setAmount(BigDecimal.valueOf(1000L + index, 2));
        driverOrder.setCreatedAt(BASE_CREATED_AT.plusSeconds(index));
        driverOrder.setUpdatedAt(BASE_CREATED_AT.plusSeconds(index + 30));
        return driverOrder;
    }
}
