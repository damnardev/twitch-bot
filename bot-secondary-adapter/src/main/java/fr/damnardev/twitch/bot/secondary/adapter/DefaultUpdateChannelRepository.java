package fr.damnardev.twitch.bot.secondary.adapter;

import fr.damnardev.twitch.bot.database.repository.DbChannelRepository;
import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.port.secondary.UpdateChannelRepository;
import fr.damnardev.twitch.bot.secondary.mapper.ChannelMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class DefaultUpdateChannelRepository implements UpdateChannelRepository {

	private final DbChannelRepository dbChannelRepository;

	private final ChannelMapper channelMapper;

	@Override
	public void update(Channel channel) {
		log.info("Updating channel {}", channel.name());
		this.dbChannelRepository.save(this.channelMapper.toEntity(channel));
		log.info("Updated channel {}", channel.name());
	}

}
