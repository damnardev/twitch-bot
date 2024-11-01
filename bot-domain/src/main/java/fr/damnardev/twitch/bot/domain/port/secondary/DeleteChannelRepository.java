package fr.damnardev.twitch.bot.domain.port.secondary;

import fr.damnardev.twitch.bot.domain.model.Channel;

public interface DeleteChannelRepository {

	void delete(Channel channel);

}
