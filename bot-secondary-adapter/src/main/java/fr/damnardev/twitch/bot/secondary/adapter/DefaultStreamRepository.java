package fr.damnardev.twitch.bot.secondary.adapter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.Stream;
import com.github.twitch4j.helix.domain.StreamList;
import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.port.secondary.StreamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class DefaultStreamRepository implements StreamRepository {

	private static final String STATUS = "live";

	private final TwitchHelix twitchHelix;

	@Override
	public List<Channel> computeOnline(List<Channel> channels) {
		log.info("Computing channels status");
		if (channels.isEmpty()) {
			return Collections.emptyList();
		}
		var onLiveMap = getOnLive(channels.stream().map(Channel::name).toList());
		channels = channels.stream().map((channel) -> {
			var status = onLiveMap.getOrDefault(channel.name(), false);
			return channel.toBuilder().online(status).build();
		}).toList();
		log.info("Channels status computed");
		return channels;
	}

	@Override
	public Channel computeOnline(Channel channel) {
		log.info("Computing status for channel: {}", channel);
		var onLiveMap = getOnLive(Collections.singletonList(channel.name()));
		var status = onLiveMap.getOrDefault(channel.name(), false);
		channel = channel.toBuilder().online(status).build();
		log.info("Status computed for channel: {}", channel);
		return channel;
	}

	private Map<String, Boolean> getOnLive(List<String> channels) {
		return callStreamApi(channels).getStreams().stream().collect(Collectors.toMap(Stream::getUserName, (stream) -> stream.getType().equalsIgnoreCase(STATUS)));
	}

	private StreamList callStreamApi(List<String> channels) {
		return this.twitchHelix.getStreams(null, null, null, null, null, null, null, channels).execute();
	}

}
