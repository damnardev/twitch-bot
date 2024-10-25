package fr.damnardev.twitch.bot.domain.port.secondary;

import fr.damnardev.twitch.bot.domain.model.ChannelInfo;

public interface IChatRepository {

	void joinAllChannel();

	void joinChannel(ChannelInfo channelInfo);

	void leaveChannel(ChannelInfo channelInfo);

	void reconnect();

}
