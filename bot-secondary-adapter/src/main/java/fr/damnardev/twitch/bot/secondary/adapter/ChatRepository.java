package fr.damnardev.twitch.bot.secondary.adapter;

import com.github.twitch4j.TwitchClient;
import fr.damnardev.twitch.bot.database.entity.Channel;
import fr.damnardev.twitch.bot.database.repository.DbChannelRepository;
import fr.damnardev.twitch.bot.domain.model.ChannelInfo;
import fr.damnardev.twitch.bot.domain.port.secondary.IChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChatRepository implements IChatRepository {

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
		channels.forEach(this::joinChannel);
	}

	@Override
	@Transactional(readOnly = true)
	public void joinChannel(ChannelInfo channelInfo) {
		var channel = this.dbChannelRepository.findById(channelInfo.user().id()).orElseThrow();
		joinChannel(channel);
	}

	@Override
	@Transactional(readOnly = true)
	public void leaveChannel(ChannelInfo channelInfo) {
		var channel = this.dbChannelRepository.findById(channelInfo.user().id()).orElseThrow();
		leaveChannel(channel);
	}

	private void joinChannel(Channel broadcaster) {
		log.info("Joining channel {}", broadcaster.getName());
		this.twitchClient.getChat().joinChannel(broadcaster.getName());
	}

	private void leaveChannel(Channel broadcaster) {
		log.info("Leaving channel {}", broadcaster.getName());
		this.twitchClient.getChat().leaveChannel(broadcaster.getName());
	}

	@Override
	public void reconnect() {
		this.twitchClient.getChat().reconnect();
	}

}
