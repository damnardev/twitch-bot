package fr.damnardev.twitch.bot.domain.port.secondary;

import fr.damnardev.twitch.bot.domain.model.ChannelInfo;
import fr.damnardev.twitch.bot.domain.model.User;

import java.util.Optional;

public interface IFindChannelRepository {

    Optional<ChannelInfo> find(User channel);

}
