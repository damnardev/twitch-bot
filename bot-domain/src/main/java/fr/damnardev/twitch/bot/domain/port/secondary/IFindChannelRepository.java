package fr.damnardev.twitch.bot.domain.port.secondary;

import java.util.List;
import java.util.Optional;

import fr.damnardev.twitch.bot.domain.model.Channel;

public interface IFindChannelRepository {

	List<Channel> findAllEnabled();

	Optional<Channel> findByName(String name);

}
