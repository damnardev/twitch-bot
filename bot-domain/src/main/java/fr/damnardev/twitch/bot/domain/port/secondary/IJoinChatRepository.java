package fr.damnardev.twitch.bot.domain.port.secondary;

import fr.damnardev.twitch.bot.domain.model.ChannelInfo;

public interface IJoinChatRepository {

	void joinAllChannel();

	void joinChannel(ChannelInfo channelInfo);

	void reconnect();

}
