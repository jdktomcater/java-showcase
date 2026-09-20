package com.jdktomcat.showcase.legacy.system.vault.ob.hikaricp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OceanBaseUsernameFormatter {

    @Value("${oceanbase.tenant-name}")
    private String tenantName;

    @Value("${oceanbase.cluster-name}")
    private String clusterName;

    public String buildOceanBaseUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return username;
        }
        String suffix = "@" + tenantName + "#" + clusterName;
        return username.endsWith(suffix) ? username : username + suffix;
    }
}
