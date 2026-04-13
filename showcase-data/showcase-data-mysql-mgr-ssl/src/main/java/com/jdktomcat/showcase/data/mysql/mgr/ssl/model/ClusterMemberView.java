package com.jdktomcat.showcase.data.mysql.mgr.ssl.model;

public record ClusterMemberView(
        String host,
        int port,
        String state,
        String role
) {
}
