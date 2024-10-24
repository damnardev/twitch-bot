package fr.damnardev.twitch.bot.domain.port.secondary;

import java.util.List;

import fr.damnardev.twitch.bot.domain.model.ChannelInfo;

public interface IFindAllChannelRepository {

	List<ChannelInfo> findAll();

}
