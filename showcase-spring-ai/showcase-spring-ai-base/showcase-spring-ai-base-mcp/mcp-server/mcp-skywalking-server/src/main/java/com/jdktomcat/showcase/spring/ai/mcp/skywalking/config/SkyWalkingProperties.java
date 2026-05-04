package com.jdktomcat.showcase.spring.ai.mcp.skywalking.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "skywalking")
public class SkyWalkingProperties {

    private String graphqlEndpoint = "http://localhost:12800/graphql";
    private Duration requestTimeout = Duration.ofSeconds(30);
    private ZoneId queryZone = ZoneId.systemDefault();
    private List<Header> headers = new ArrayList<>();

    @Getter
    @Setter
    public static class Header {
        private String name;
        private String value;
    }
}
