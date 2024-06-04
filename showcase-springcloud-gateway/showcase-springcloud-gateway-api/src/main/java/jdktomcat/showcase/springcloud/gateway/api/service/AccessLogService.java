package jdktomcat.showcase.springcloud.gateway.api.service;

import jdktomcat.showcase.springcloud.gateway.api.log.GatewayLog;

public interface AccessLogService {
    void saveAccessLog(GatewayLog gatewayLog);
}
