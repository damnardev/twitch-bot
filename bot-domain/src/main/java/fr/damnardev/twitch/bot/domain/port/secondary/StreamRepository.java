package fr.damnardev.twitch.bot.domain.port.secondary;

import java.util.List;

import fr.damnardev.twitch.bot.domain.model.Channel;

public interface StreamRepository {

	List<Channel> computeOnline(List<Channel> channels);

	Channel computeOnline(Channel channel);

}
