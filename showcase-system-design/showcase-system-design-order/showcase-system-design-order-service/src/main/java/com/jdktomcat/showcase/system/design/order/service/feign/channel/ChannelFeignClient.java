package com.jdktomcat.showcase.system.design.order.service.feign.channel;

import com.jdktomcat.showcase.system.design.channel.service.api.client.domain.Channel;
import com.jdktomcat.showcase.system.design.common.web.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(name = "channel-service", fallbackFactory = ChannelFeignFallbackFactory.class)
public interface ChannelFeignClient {
    /**
     * get channel info by channel id
     *
     * @param channelId channel id
     * @return channel
     */
    @GetMapping("/channel/get")
    Response<Channel> getChannel(@RequestParam(value = "channelId") Long channelId);
}
