package fr.damnardev.twitch.bot.domain.port.secondary;

import fr.damnardev.twitch.bot.domain.model.ChannelInfo;

public interface ISaveChannelRepository {

    ChannelInfo save(ChannelInfo channel);

}
