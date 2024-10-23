package fr.damnardev.twitch.bot.domain.port.secondary;

import fr.damnardev.twitch.bot.domain.model.ChannelCommand;
import fr.damnardev.twitch.bot.domain.model.ChannelCommandConfiguration;

public interface IFindCommandConfigurationRepository {

    ChannelCommandConfiguration find(ChannelCommand channelCommand);

}
