package fr.damnardev.twitch.bot.domain.port.secondary;

import fr.damnardev.twitch.bot.domain.model.ChannelInfo;

public interface IDeleteChannelRepository {

	void delete(ChannelInfo channelInfo);

}
