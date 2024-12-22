package fr.damnardev.twitch.bot.port.secondary.channel;

import fr.damnardev.twitch.bot.model.Channel;

public interface SaveChannelRepository {

	Channel save(Channel channel);

}
