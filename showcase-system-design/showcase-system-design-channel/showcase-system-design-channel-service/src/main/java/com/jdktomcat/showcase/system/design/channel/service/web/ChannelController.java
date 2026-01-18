package com.jdktomcat.showcase.system.design.channel.service.web;

import com.jdktomcat.showcase.system.design.channel.service.api.client.ChannelApi;
import com.jdktomcat.showcase.system.design.channel.service.api.client.domain.Channel;
import com.jdktomcat.showcase.system.design.common.web.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * channel controller entrance
 */
@Slf4j
@RestController
public class ChannelController implements ChannelApi {

    /**
     * implement the controller
     *
     * @param channelId channel id
     * @return response
     */
    @Override
    public Response<Channel> getChannel(Long channelId) {
        Channel channel = new Channel();
        channel.setId(channelId);
        channel.setName("channel"+channelId);
        return Response.success(channel);
    }
}
