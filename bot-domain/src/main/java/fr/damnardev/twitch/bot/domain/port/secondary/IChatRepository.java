package fr.damnardev.twitch.bot.domain.port.secondary;

import java.util.List;

import fr.damnardev.twitch.bot.domain.model.Channel;

public interface IChatRepository {

	void joinAll(List<Channel> channel);

	void reconnect();

}
