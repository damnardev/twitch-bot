package fr.damnardev.twitch.bot.port.secondary.channel;

import fr.damnardev.twitch.bot.model.Channel;

public interface DeleteChannelRepository {

	void delete(Channel channel);

}
