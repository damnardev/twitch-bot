package fr.damnardev.twitch.bot.domain.port.secondary;

import fr.damnardev.twitch.bot.domain.model.ChannelRaidConfiguration;
import fr.damnardev.twitch.bot.domain.model.User;

public interface IFindChannelRaidConfigurationRepository {

	ChannelRaidConfiguration find(User channel);

}
