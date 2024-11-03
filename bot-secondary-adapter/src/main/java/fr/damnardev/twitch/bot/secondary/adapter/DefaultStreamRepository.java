package fr.damnardev.twitch.bot.secondary.adapter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.Stream;
import com.github.twitch4j.helix.domain.StreamList;
import fr.damnardev.twitch.bot.database.entity.DbChannel;
import fr.damnardev.twitch.bot.database.repository.DbChannelRepository;
import fr.damnardev.twitch.bot.domain.exception.FatalException;
import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.port.secondary.StreamRepository;
import fr.damnardev.twitch.bot.secondary.mapper.ChannelMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class DefaultStreamRepository implements StreamRepository {

	private static final String STATUS = "live";

	private final DbChannelRepository dbChannelRepository;

	private final TwitchHelix twitchHelix;

	private final ChannelMapper channelMapper;

	@Override
	@Transactional
	public List<Channel> computeAll(List<Channel> channels) {
		log.info("Computing channels status");
		if (channels.isEmpty()) {
			return Collections.emptyList();
		}
		var dbChannels = this.dbChannelRepository.findAllById(channels.stream().map(Channel::id).toList());
		var onLiveMap = getOnLive(dbChannels.stream().map(DbChannel::getName).toList());
		dbChannels.forEach((channel) -> {
			var status = onLiveMap.getOrDefault(channel.getName(), false);
			channel.setOnline(status);
		});
		dbChannels = this.dbChannelRepository.saveAllAndFlush(dbChannels);
		channels = dbChannels.stream().map(this.channelMapper::toModel).toList();
		log.info("Channels status computed");
		return channels;
	}

	@Override
	@Transactional
	public Channel compute(Channel channel) {
		log.info("Computing status for channel: {}", channel);
		var dbChannel = this.dbChannelRepository.findById(channel.id()).orElseThrow(() -> new FatalException("Channel not found"));
		var onLiveMap = getOnLive(Collections.singletonList(dbChannel.getName()));
		var status = onLiveMap.getOrDefault(dbChannel.getName(), false);
		dbChannel.setOnline(status);
		dbChannel = this.dbChannelRepository.saveAndFlush(dbChannel);
		channel = this.channelMapper.toModel(dbChannel);
		log.info("Status computed for channel: {}", channel);
		return channel;
	}

	private Map<String, Boolean> getOnLive(List<String> channels) {
		return callStreamApi(channels).getStreams().stream().collect(Collectors.toMap(Stream::getUserLogin, (stream) -> stream.getType().equalsIgnoreCase(STATUS)));
	}

	private StreamList callStreamApi(List<String> channels) {
		return this.twitchHelix.getStreams(null, null, null, null, null, null, null, channels).execute();
	}

}
