package com.jdktomcat.showcase.legacy.system.vault.simple.controller;

import com.alibaba.druid.pool.DruidDataSource;
import com.jdktomcat.showcase.legacy.system.vault.simple.mapper.HealthMapper;
import org.springframework.aop.framework.Advised;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@RestController
public class DataSourceDebugController {

    private final DataSource dataSource;
    private final HealthMapper healthMapper;
    private final ContextRefresher contextRefresher;

    public DataSourceDebugController(DataSource dataSource, HealthMapper healthMapper, ContextRefresher contextRefresher) {
        this.dataSource = dataSource;
        this.healthMapper = healthMapper;
        this.contextRefresher = contextRefresher;
    }

    @GetMapping("/ds-info")
    public Map<String, Object> dataSourceInfo() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("dbCurrentUser", healthMapper.currentUser());
        m.put("dataSourceClass", dataSource.getClass().getName());

        DruidDataSource druid = unwrapDruid(dataSource);
        if (druid != null) {
            m.put("dsName", druid.getName());
            m.put("dsUsername", druid.getUsername());
            m.put("activeCount", druid.getActiveCount());
            m.put("poolingCount", druid.getPoolingCount());
        }
        return m;
    }

    @PostMapping("/refresh")
    public Set<String> refresh() {
        return contextRefresher.refresh();
    }

    @PostMapping("/druid/restart")
    public Map<String, Object> restartDruid() throws Exception {
        DruidDataSource druid = unwrapDruid(dataSource);
        if (druid == null) {
            return dataSourceInfo();
        }
        druid.close();
        druid.init();
        return dataSourceInfo();
    }

    private static DruidDataSource unwrapDruid(Object candidate) {
        if (candidate == null) {
            return null;
        }
        if (candidate instanceof DruidDataSource) {
            return (DruidDataSource) candidate;
        }
        if (candidate instanceof Advised) {
            try {
                Object target = ((Advised) candidate).getTargetSource().getTarget();
                return unwrapDruid(target);
            } catch (Exception ignored) {
                return null;
            }
        }
        return null;
    }
}

