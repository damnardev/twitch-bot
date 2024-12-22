package fr.damnardev.twitch.bot.secondary.adapter.channel;

import java.util.List;

import fr.damnardev.twitch.bot.database.repository.DbChannelRepository;
import fr.damnardev.twitch.bot.model.Channel;
import fr.damnardev.twitch.bot.port.secondary.channel.UpdateChannelRepository;
import fr.damnardev.twitch.bot.secondary.mapper.ChannelMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class DefaultUpdateChannelRepository implements UpdateChannelRepository {

	private final DbChannelRepository dbChannelRepository;

	private final ChannelMapper channelMapper;

	@Override
	@Transactional
	public void update(Channel channel) {
		log.info("Updating channel {}", channel.name());
		this.dbChannelRepository.save(this.channelMapper.toEntity(channel));
		log.info("Updated channel {}", channel.name());
	}

	@Override
	@Transactional
	public void updateAll(List<Channel> channels) {
		log.info("Updating channels {}", channels);
		this.dbChannelRepository.saveAll(channels.stream().map(this.channelMapper::toEntity).toList());
		log.info("Channels updated {}", channels);
	}

}
