package com.jdktomcat.showcase.system.design.channel.service.api.client;

import com.jdktomcat.showcase.system.design.channel.service.api.client.domain.Channel;

import java.util.Optional;

public interface ChannelApi {

    /**
     * get channel info by channel id
     *
     * @param channelId channel id
     * @return channel
     */

    public Response<Channel> getChannel(Long channelId);
}
