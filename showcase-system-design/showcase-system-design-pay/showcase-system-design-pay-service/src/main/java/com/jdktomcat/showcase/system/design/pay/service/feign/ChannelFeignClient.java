package com.jdktomcat.showcase.system.design.pay.service.feign;

import com.jdktomcat.showcase.system.design.channel.service.api.client.ChannelApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

@Component
@FeignClient(name = "channel-service")
public interface ChannelFeignClient extends ChannelApi {

}
