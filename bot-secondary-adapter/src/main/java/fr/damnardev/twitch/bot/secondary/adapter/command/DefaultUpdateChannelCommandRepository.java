package fr.damnardev.twitch.bot.secondary.adapter.command;

import fr.damnardev.twitch.bot.database.repository.DbChannelCommandRepository;
import fr.damnardev.twitch.bot.domain.model.Command;
import fr.damnardev.twitch.bot.domain.port.secondary.command.UpdateChannelCommandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class DefaultUpdateChannelCommandRepository implements UpdateChannelCommandRepository {

	private final DbChannelCommandRepository dbChannelCommandRepository;

	@Override
	@Transactional
	public void update(Command command) {
		log.info("Updating command {}", command.name());
		this.dbChannelCommandRepository.findByChannelNameAndName(command.channelName(), command.name()).ifPresent((dbChannelCommand) -> {
			dbChannelCommand.setEnabled(command.enabled());
			dbChannelCommand.setCooldown(command.cooldown());
			dbChannelCommand.setLastExecution(command.lastExecution());
			dbChannelCommand.getMessages().clear();
			dbChannelCommand.getMessages().addAll(command.messages());
			this.dbChannelCommandRepository.save(dbChannelCommand);
		});
		log.info("Updated command {}", command.name());
	}

}
