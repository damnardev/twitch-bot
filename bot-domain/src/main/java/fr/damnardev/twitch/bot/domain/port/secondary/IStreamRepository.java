package fr.damnardev.twitch.bot.domain.port.secondary;

import fr.damnardev.twitch.bot.domain.model.ChannelInfo;

public interface IStreamRepository {

	void computeStatus();

	void computeStatus(ChannelInfo channelInfo);

}
