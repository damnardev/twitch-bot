package fr.damnardev.twitch.bot.secondary.adapter;

import java.time.OffsetDateTime;

import fr.damnardev.twitch.bot.database.repository.DbChannelCommandRepository;
import fr.damnardev.twitch.bot.domain.model.ChannelCommandConfiguration;
import fr.damnardev.twitch.bot.domain.port.secondary.ISaveCommandConfigurationRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SaveCommandConfigurationRepository implements ISaveCommandConfigurationRepository {

	private final DbChannelCommandRepository dbChannelCommandRepository;

	@Override
	@Transactional
	public void updateLastExecution(ChannelCommandConfiguration channelCommandConfiguration) {
		var entity = this.dbChannelCommandRepository.findById(channelCommandConfiguration.id());
		entity.ifPresent((command) -> {
			command.setLastExecution(OffsetDateTime.now());
			this.dbChannelCommandRepository.saveAndFlush(command);
		});
	}

}
