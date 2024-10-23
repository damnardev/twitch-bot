package fr.damnardev.twitch.bot.secondary.adapter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.domain.Stream;
import com.github.twitch4j.helix.domain.StreamList;
import fr.damnardev.twitch.bot.database.entity.Channel;
import fr.damnardev.twitch.bot.database.repository.DbChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.IStreamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class StreamRepository implements IStreamRepository {

	public static final String STATUS = "live";

	private final DbChannelRepository dbChannelRepository;

	private final TwitchClient twitchClient;

	@Override
	@Transactional
	public void computeStatus() {
		log.info("Computing channels status");
		var channels = this.dbChannelRepository.findAllEnabled();
		if (channels.isEmpty()) {
			return;
		}
		computeAllStatus(channels);
		this.dbChannelRepository.saveAllAndFlush(channels);
		log.info("Channels status computed");
	}

	private void computeAllStatus(List<Channel> channels) {
		var onLiveMap = getOnLive(channels.stream().map(Channel::getName).toList());
		channels.forEach((channel) -> {
			var status = onLiveMap.getOrDefault(channel.getName(), false);
			channel.setOnline(status);
		});
	}

	public Map<String, Boolean> getOnLive(List<String> channels) {
		return callStreamApi(channels).getStreams().stream().collect(Collectors.toMap(Stream::getUserLogin, (stream) -> stream.getType().equalsIgnoreCase(STATUS)));
	}

	private StreamList callStreamApi(List<String> channels) {
		return this.twitchClient.getHelix().getStreams(null, null, null, null, null, null, null, channels).execute();
	}

}
