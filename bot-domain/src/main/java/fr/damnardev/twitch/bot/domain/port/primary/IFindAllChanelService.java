package fr.damnardev.twitch.bot.domain.port.primary;

import java.util.List;

import fr.damnardev.twitch.bot.domain.model.ChannelInfo;

public interface IFindAllChanelService {

	List<ChannelInfo> findAll();

}
