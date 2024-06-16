package com.jdktomcat.showcase.rest.conf;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class RemoteConfig {
    @Value("${remote.url}")
    private String remoteUrl;
}
