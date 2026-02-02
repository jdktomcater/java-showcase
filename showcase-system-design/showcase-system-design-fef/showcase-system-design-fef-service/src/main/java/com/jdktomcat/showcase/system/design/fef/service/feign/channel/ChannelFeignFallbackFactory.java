package com.jdktomcat.showcase.system.design.fef.service.feign.channel;

import com.jdktomcat.showcase.system.design.common.web.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * factory
 */
@Component
@Slf4j
public class ChannelFeignFallbackFactory implements FallbackFactory<ChannelFeignClient> {
    @Override
    public ChannelFeignClient create(Throwable cause) {
        log.error("occur some error", cause);
        return channelId -> Response.fail(null);
    }
}
