package fr.damnardev.twitch.bot.secondary.adapter;

import fr.damnardev.twitch.bot.database.repository.DbChannelRepository;
import fr.damnardev.twitch.bot.domain.model.ChannelInfo;
import fr.damnardev.twitch.bot.domain.port.secondary.IUpdateChannelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class UpdateChannelRepository implements IUpdateChannelRepository {

	private final DbChannelRepository repository;

	@Override
	public void update(ChannelInfo channel) {
		log.info("Updating channel {}", channel);
		this.repository.findById(channel.user().id()).ifPresent((entity) -> {
			entity.setBotEnabled(channel.enabled());
			this.repository.save(entity);
		});
	}

	@Override
	public void updateStatus(ChannelInfo channel) {
		log.info("Updating channel {}", channel);
		this.repository.findById(channel.user().id()).ifPresent((entity) -> {
			entity.setOnline(channel.online());
			this.repository.save(entity);
		});
	}

}
