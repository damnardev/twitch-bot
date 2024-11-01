package fr.damnardev.twitch.bot.domain.port.secondary;

import fr.damnardev.twitch.bot.domain.model.Channel;

public interface UpdateChannelRepository {

	void update(Channel channel);

}
