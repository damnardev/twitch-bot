package fr.damnardev.twitch.bot.domain.port.secondary.channel;

import fr.damnardev.twitch.bot.domain.model.Channel;

public interface DeleteChannelRepository {

	void delete(Channel channel);

}
