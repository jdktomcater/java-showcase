package com.jdktomcat.showcase.data.mysql.mgr.ssl.model;

public class ClusterMemberView {

    private final String host;
    private final int port;
    private final String state;
    private final String role;

    public ClusterMemberView(String host, int port, String state, String role) {
        this.host = host;
        this.port = port;
        this.state = state;
        this.role = role;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getState() {
        return state;
    }

    public String getRole() {
        return role;
    }
}
