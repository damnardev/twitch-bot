package fr.damnardev.twitch.bot.domain.port.secondary;

import fr.damnardev.twitch.bot.domain.model.ChannelCommandConfiguration;

public interface ISaveCommandConfigurationRepository {

    void updateLastExecution(ChannelCommandConfiguration channelCommandConfiguration);

}
