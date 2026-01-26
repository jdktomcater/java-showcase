package com.jdktomcat.showcase.legacy.system.vault.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.jdktomcat.showcase.legacy.system.vault.job")
@Slf4j
public class XxlJobConfig {
    @Value("${xxl.job.admin.addresses: }")
    private String adminAddresses;
    @Value("${xxl.job.executor.appname: }")
    private String appName;
    @Value("${xxl.job.executor.ip: }")
    private String ip;
    @Value("${xxl.job.executor.port:-1}")
    private Integer port;
    @Value("${xxl.job.accessToken: }")
    private String accessToken;
    @Value("${xxl.job.executor.logpath: }")
    private String logPath;
    @Value("${xxl.job.executor.logretentiondays:7}")
    private int logRetentionDays;

    @Bean(initMethod = "start", destroyMethod = "destroy")
    public XxlJobSpringExecutor xxlJobExecutor() {
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        try {
            xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
            xxlJobSpringExecutor.setAppName(appName);
            xxlJobSpringExecutor.setIp(ip);
            xxlJobSpringExecutor.setPort(port);
            xxlJobSpringExecutor.setAccessToken(accessToken);
            xxlJobSpringExecutor.setLogPath(logPath);
            xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return xxlJobSpringExecutor;
    }
}
