package fr.damnardev.twitch.bot.domain.port.secondary;

import java.util.List;

import fr.damnardev.twitch.bot.domain.model.Channel;

public interface UpdateChannelRepository {

	void update(Channel channel);

	void updateAll(List<Channel> channels);

}
