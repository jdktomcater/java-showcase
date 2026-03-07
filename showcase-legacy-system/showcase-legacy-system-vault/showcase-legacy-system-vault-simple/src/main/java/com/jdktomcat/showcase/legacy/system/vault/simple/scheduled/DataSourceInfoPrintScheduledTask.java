package com.jdktomcat.showcase.legacy.system.vault.simple.scheduled;

import com.alibaba.druid.pool.DruidDataSource;
import com.jdktomcat.showcase.legacy.system.vault.simple.mapper.HealthMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 *  scheduled task:print datasource info
 */
@Slf4j
@Component
public class DataSourceInfoPrintScheduledTask {

    private final DataSource dataSource;
    private final HealthMapper healthMapper;

    public DataSourceInfoPrintScheduledTask(DataSource dataSource, HealthMapper healthMapper) {
        this.dataSource = dataSource;
        this.healthMapper = healthMapper;
    }

    @Scheduled(initialDelay = 5_000L, fixedDelay = 30_000L)
    public void print() {
        String currentUser;
        try {
            currentUser = healthMapper.currentUser();
        } catch (Exception e) {
            log.warn("query current_user() failed", e);
            return;
        }
        if (dataSource instanceof DruidDataSource) {
            DruidDataSource druid = (DruidDataSource) dataSource;
            log.info(
                    "datasource={} dsUser={} active={} pooling={} currentUser={}",
                    druid.getName(),
                    druid.getUsername(),
                    druid.getActiveCount(),
                    druid.getPoolingCount(),
                    currentUser
            );
            return;
        }
        log.info("datasource={} currentUser={}", dataSource.getClass().getName(), currentUser);
    }
}
