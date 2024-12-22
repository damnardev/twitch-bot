package fr.damnardev.twitch.bot.secondary.adapter.channel;

import java.util.Collections;

import com.github.twitch4j.helix.TwitchHelix;
import fr.damnardev.twitch.bot.database.entity.DbChannel;
import fr.damnardev.twitch.bot.database.entity.DbRaidConfiguration;
import fr.damnardev.twitch.bot.database.repository.DbChannelRepository;
import fr.damnardev.twitch.bot.model.Channel;
import fr.damnardev.twitch.bot.port.secondary.channel.SaveChannelRepository;
import fr.damnardev.twitch.bot.secondary.mapper.ChannelMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class DefaultSaveChannelRepository implements SaveChannelRepository {

	private final DbChannelRepository dbChannelRepository;

	private final TwitchHelix twitchHelix;

	private final ChannelMapper channelMapper;

	@Override
	@Transactional
	public Channel save(Channel channel) {
		log.info("Creating channel {}", channel.name());
		var user = this.twitchHelix.getUsers(null, null, Collections.singletonList(channel.name())).execute().getUsers().stream().findFirst().orElseThrow(() -> new RuntimeException("User %s not found".formatted(channel.name())));
		var id = Long.parseLong(user.getId());
		var dbChannel = DbChannel.builder().id(id).name(user.getDisplayName()).build();
		var dbRaidConfiguration = DbRaidConfiguration.builder().channel(dbChannel).build();
		dbChannel.setRaidConfiguration(dbRaidConfiguration);
		dbChannel = this.dbChannelRepository.save(dbChannel);
		log.info("Created channel {}", channel.name());
		return this.channelMapper.toModel(dbChannel);
	}

}
