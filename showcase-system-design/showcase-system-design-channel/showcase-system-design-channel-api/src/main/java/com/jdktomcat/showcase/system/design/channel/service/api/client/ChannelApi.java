package com.jdktomcat.showcase.system.design.channel.service.api.client;

import com.jdktomcat.showcase.system.design.channel.service.api.client.domain.Channel;
import com.jdktomcat.showcase.system.design.common.web.response.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RequestMapping("/channel")
public interface ChannelApi {

    /**
     * get channel info by channel id
     *
     * @param channelId channel id
     * @return channel
     */
    @GetMapping("/hello")
    Response<Channel> getChannel(@RequestParam(value = "channelId") Long channelId);
}
