package com.jdktomcat.showcase.data.mysql.mgr.ssl.model;

public record RecoveryChannelView(
        String channelName,
        String sourceHost,
        Integer sourcePort,
        String replicationUser,
        String sslAllowed,
        String tlsVersion,
        String sslCaFile,
        String verifyServerCertificate
) {
}
