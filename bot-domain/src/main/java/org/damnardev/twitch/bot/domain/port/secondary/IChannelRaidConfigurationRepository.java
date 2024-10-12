package org.damnardev.twitch.bot.domain.port.secondary;

import org.damnardev.twitch.bot.domain.model.ChannelInfo;
import org.damnardev.twitch.bot.domain.model.ChannelRaidConfiguration;

public interface IChannelRaidConfigurationRepository {

    ChannelRaidConfiguration find(ChannelInfo channel);

}
