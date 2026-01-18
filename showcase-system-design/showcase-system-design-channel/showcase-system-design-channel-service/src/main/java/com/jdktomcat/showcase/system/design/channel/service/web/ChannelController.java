package com.jdktomcat.showcase.system.design.channel.service.web;

import com.jdktomcat.showcase.system.design.channel.service.api.client.domain.Channel;
import com.jdktomcat.showcase.system.design.common.web.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * channel controller entrance
 */
@Slf4j
@RestController
@RequestMapping(value = "/channel")
public class ChannelController{

    /**
     * implement the controller
     *
     * @param channelId channel id
     * @return response
     */
    @GetMapping("/get")
    Response<Channel> getChannel(@RequestParam(value = "channelId") Long channelId){
        Channel channel = new Channel();
        channel.setId(channelId);
        channel.setName("channel"+channelId);
        return Response.success(channel);
    }
}
