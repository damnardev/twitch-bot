package fr.damnardev.twitch.bot.secondary.adapter;

import java.util.List;

import com.github.twitch4j.TwitchClientHelper;
import com.github.twitch4j.chat.TwitchChat;
import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.port.secondary.IChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChatRepository implements IChatRepository {

	private final TwitchChat twitchChat;

	private final TwitchClientHelper twitchClientHelper;

	@Override
	public void joinAll(List<Channel> channel) {
		log.info("Joining all channels");
		channel.stream().forEach(this::join);
	}

	private void join(Channel channel) {
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

}
