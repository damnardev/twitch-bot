package fr.damnardev.twitch.bot.secondary.adapter;

import com.github.twitch4j.TwitchClient;
import fr.damnardev.twitch.bot.database.repository.DbChannelRepository;
import fr.damnardev.twitch.bot.domain.model.ChannelInfo;
import fr.damnardev.twitch.bot.domain.port.secondary.IJoinChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class JoinChatRepository implements IJoinChatRepository {

	private final TwitchClient twitchClient;

	private final DbChannelRepository dbChannelRepository;

	@Override
	@Transactional(readOnly = true)
	public void joinAllChannel() {
		log.info("Joining all channels");
		var channels = this.dbChannelRepository.findAllEnabled();
		if (channels.isEmpty()) {
			return;
		}
		channels.forEach((channel) -> joinChannel(channel.getId().toString(), channel.getName()));
	}

	@Override
	public void joinChannel(ChannelInfo channelInfo) {
		joinChannel(channelInfo.user().id().toString(), channelInfo.user().name());
	}

	private void joinChannel(String channelId, String channelName) {
		log.info("Joining channel {}", channelName);
		this.twitchClient.getChat().joinChannel(channelName);
		this.twitchClient.getClientHelper().enableStreamEventListener(channelId, channelName);
	}

	@Override
	public void reconnect() {
		this.twitchClient.getChat().reconnect();
	}

}
