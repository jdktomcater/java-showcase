package com.jdktomcat.showcase.system.design.pay.service.feign.channel;

import com.jdktomcat.showcase.system.design.channel.service.api.client.domain.Channel;
import com.jdktomcat.showcase.system.design.common.web.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * fall back when remote service unavailable
 */
@Component
@Slf4j
public class ChannelFeignFallback implements ChannelFeignClient {

    @Override
    public Response<Channel> getChannel(Long channelId) {
        log.error("getChannel error");
        return Response.fail(null);
    }
}
