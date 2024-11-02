package fr.damnardev.twitch.bot.secondary.adapter;

import java.util.List;

import com.github.twitch4j.TwitchClientHelper;
import com.github.twitch4j.chat.TwitchChat;
import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.port.secondary.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class DefaultChatRepository implements ChatRepository {

	private final TwitchChat twitchChat;

	private final TwitchClientHelper twitchClientHelper;

	@Override
	public void joinAll(List<Channel> channels) {
		log.info("Joining all channels");
		channels.forEach(this::join);
	}

	@Override
	public void join(Channel channel) {
		log.info("Joining channel {}", channel.name());
		this.twitchChat.joinChannel(channel.name());
		this.twitchClientHelper.enableStreamEventListener(channel.id().toString(), channel.name());
		log.info("Joined channel {}", channel.name());
	}

	@Override
	public void reconnect() {
		log.info("Reconnecting to chat");
		this.twitchChat.reconnect();
		log.info("Reconnected to chat");
	}

	@Override
	public void leave(Channel channel) {
		log.info("Leaving channel {}", channel.name());
		this.twitchChat.leaveChannel(channel.name());
		this.twitchClientHelper.disableStreamEventListener(channel.name());
		log.info("Left channel {}", channel.name());
	}

}
