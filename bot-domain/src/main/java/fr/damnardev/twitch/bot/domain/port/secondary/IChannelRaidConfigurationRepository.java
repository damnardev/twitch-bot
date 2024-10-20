package fr.damnardev.twitch.bot.domain.port.secondary;

import fr.damnardev.twitch.bot.domain.model.ChannelInfo;
import fr.damnardev.twitch.bot.domain.model.ChannelRaidConfiguration;

public interface IChannelRaidConfigurationRepository {

    ChannelRaidConfiguration find(ChannelInfo channel);

}
