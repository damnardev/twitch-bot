package fr.damnardev.twitch.bot.domain.port.secondary;

import java.util.Optional;

import fr.damnardev.twitch.bot.domain.model.ChannelInfo;
import fr.damnardev.twitch.bot.domain.model.User;

public interface IFindChannelRepository {

	Optional<ChannelInfo> find(User channel);

}
