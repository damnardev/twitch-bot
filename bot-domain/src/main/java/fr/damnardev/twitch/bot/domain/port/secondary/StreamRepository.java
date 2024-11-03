package fr.damnardev.twitch.bot.domain.port.secondary;

import java.util.List;

import fr.damnardev.twitch.bot.domain.model.Channel;

public interface StreamRepository {

	List<Channel> computeAll(List<Channel> channels);

	Channel compute(Channel channel);

}
