package fr.damnardev.twitch.bot.secondary.adapter.channel;

import fr.damnardev.twitch.bot.database.repository.DbChannelRepository;
import fr.damnardev.twitch.bot.model.Channel;
import fr.damnardev.twitch.bot.port.secondary.channel.DeleteChannelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class DefaultDeleteChannelRepository implements DeleteChannelRepository {

	private final DbChannelRepository dbChannelRepository;

	@Override
	@Transactional
	public void delete(Channel channel) {
		log.info("Deleting channel {}", channel.name());
		this.dbChannelRepository.deleteById(channel.id());
		log.info("Deleted channel {}", channel.name());
	}

}
