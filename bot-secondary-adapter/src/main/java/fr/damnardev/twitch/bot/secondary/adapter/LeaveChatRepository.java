package fr.damnardev.twitch.bot.secondary.adapter;

import com.github.twitch4j.TwitchClient;
import fr.damnardev.twitch.bot.domain.model.ChannelInfo;
import fr.damnardev.twitch.bot.domain.port.secondary.ILeaveChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class LeaveChatRepository implements ILeaveChatRepository {

	private final TwitchClient twitchClient;

	@Override
	public void leaveChannel(ChannelInfo channelInfo) {
		String channelName = channelInfo.user().name();
		log.info("Leaving channel {}", channelName);
		this.twitchClient.getChat().leaveChannel(channelName);
		this.twitchClient.getClientHelper().disableStreamEventListenerForId(channelInfo.user().id().toString());
	}

}
