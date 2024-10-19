package org.damnardev.twitch.bot.domain.port.primary;

import org.damnardev.twitch.bot.domain.model.ChannelInfo;

public interface IAddChanelService {

    ChannelInfo process(ChannelInfo channel);

}
