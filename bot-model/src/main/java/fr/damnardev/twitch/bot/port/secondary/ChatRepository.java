package fr.damnardev.twitch.bot.port.secondary;

import java.util.List;

import fr.damnardev.twitch.bot.model.Channel;

public interface ChatRepository {

	void joinAll(List<Channel> channels);

	void join(Channel channel);

	void reconnect();

	void leave(Channel channel);

}
