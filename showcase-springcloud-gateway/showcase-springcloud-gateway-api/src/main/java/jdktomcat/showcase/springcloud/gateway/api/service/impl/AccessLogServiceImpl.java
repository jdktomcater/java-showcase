package jdktomcat.showcase.springcloud.gateway.api.service.impl;

import jdktomcat.showcase.springcloud.gateway.api.log.GatewayLog;
import jdktomcat.showcase.springcloud.gateway.api.service.AccessLogService;
import lombok.Data;
import org.springframework.stereotype.Service;

@Data
@Service
public class AccessLogServiceImpl implements AccessLogService {
    @Override
    public void saveAccessLog(GatewayLog gatewayLog) {

    }
}
