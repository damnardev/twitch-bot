package fr.damnardev.twitch.bot.domain.port.secondary;

import fr.damnardev.twitch.bot.domain.model.ChannelInfo;

public interface ILeaveChatRepository {

	void leaveChannel(ChannelInfo channelInfo);

}
