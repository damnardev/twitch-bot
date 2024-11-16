package fr.damnardev.twitch.bot.domain.port.secondary.channel;

import fr.damnardev.twitch.bot.domain.model.Channel;

public interface SaveChannelRepository {

	Channel save(Channel channel);

}
