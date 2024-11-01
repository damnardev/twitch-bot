package fr.damnardev.twitch.bot.domain.port.secondary;

import java.util.List;

import fr.damnardev.twitch.bot.domain.model.Channel;

public interface ChatRepository {

	void joinAll(List<Channel> channel);

	void join(Channel channel);

	void reconnect();

	void leave(Channel channel);

}
