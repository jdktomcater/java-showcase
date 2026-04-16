package com.jdktomcat.showcase.data.mysql.mgr.ssl.model;

public class RecoveryChannelView {

    private final String channelName;
    private final String sourceHost;
    private final Integer sourcePort;
    private final String replicationUser;
    private final String sslAllowed;
    private final String tlsVersion;
    private final String sslCaFile;
    private final String verifyServerCertificate;

    public RecoveryChannelView(
            String channelName,
            String sourceHost,
            Integer sourcePort,
            String replicationUser,
            String sslAllowed,
            String tlsVersion,
            String sslCaFile,
            String verifyServerCertificate
    ) {
        this.channelName = channelName;
        this.sourceHost = sourceHost;
        this.sourcePort = sourcePort;
        this.replicationUser = replicationUser;
        this.sslAllowed = sslAllowed;
        this.tlsVersion = tlsVersion;
        this.sslCaFile = sslCaFile;
        this.verifyServerCertificate = verifyServerCertificate;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getSourceHost() {
        return sourceHost;
    }

    public Integer getSourcePort() {
        return sourcePort;
    }

    public String getReplicationUser() {
        return replicationUser;
    }

    public String getSslAllowed() {
        return sslAllowed;
    }

    public String getTlsVersion() {
        return tlsVersion;
    }

    public String getSslCaFile() {
        return sslCaFile;
    }

    public String getVerifyServerCertificate() {
        return verifyServerCertificate;
    }
}
