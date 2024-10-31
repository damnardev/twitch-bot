package fr.damnardev.twitch.bot.domain.port.primary;

import java.util.List;

import fr.damnardev.twitch.bot.domain.model.Channel;

public interface IFindAllChannelService {

	List<Channel> findAll();

}
