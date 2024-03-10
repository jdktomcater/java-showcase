package jdktomcat.demo.skywalking.gateway.service.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * @author ZF-Timmy
 * @version V1.0
 * @description 类描述：配置类
 * @date 2024/1/13
 */

@Slf4j
@Component
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "sync")
public class UserSyncConfig {

    private Boolean flag = false;

    private Integer lease = 1;

    private String tips = "请等待上一次操作完成后重试！";

    private List<InterceptRule> rules;

    @PostConstruct
    public void init() {
        log.info("flag:{}", flag);
        log.info("lease:{}", lease);
        log.info("tips:{}", tips);
        log.info("rules:{}", Arrays.toString(rules.toArray()));
    }

    @Data
    @NoArgsConstructor
    static class InterceptRule {
        String path;
        String condition;
        Integer permits;

        public InterceptRule(String path, String condition, Integer permits) {
            this.path = path;
            this.condition = condition;
            this.permits = permits;
        }
    }
}
