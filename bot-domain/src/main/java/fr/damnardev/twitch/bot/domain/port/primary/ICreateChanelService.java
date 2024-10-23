package fr.damnardev.twitch.bot.domain.port.primary;

import fr.damnardev.twitch.bot.domain.model.ChannelInfo;

public interface ICreateChanelService {

    ChannelInfo create(ChannelInfo channelInfo);

}
