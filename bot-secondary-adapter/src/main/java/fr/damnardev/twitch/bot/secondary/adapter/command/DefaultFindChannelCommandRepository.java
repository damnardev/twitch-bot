package fr.damnardev.twitch.bot.secondary.adapter.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.damnardev.twitch.bot.database.entity.DbChannelCommand;
import fr.damnardev.twitch.bot.database.repository.DbChannelCommandRepository;
import fr.damnardev.twitch.bot.model.Channel;
import fr.damnardev.twitch.bot.model.Command;
import fr.damnardev.twitch.bot.port.secondary.command.FindChannelCommandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class DefaultFindChannelCommandRepository implements FindChannelCommandRepository {

	private final DbChannelCommandRepository dbChannelCommandRepository;

	private static @NotNull List<String> getMessages(DbChannelCommand dbChannelCommand) {
		if (dbChannelCommand.getMessages() != null) {
			return new ArrayList<>(dbChannelCommand.getMessages());
		}
		return new ArrayList<>();
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Command> findByChannelAndName(Channel channel, String name) {
		return dbChannelCommandRepository.findByChannelNameAndName(channel.name(), name).map(this::toModel);
	}

	private Command toModel(DbChannelCommand dbChannelCommand) {
		return Command.builder().channelId(dbChannelCommand.getId()).channelName(dbChannelCommand.getChannel().getName()).name(dbChannelCommand.getName()).type(dbChannelCommand.getType()).enabled(dbChannelCommand.isEnabled()).cooldown(dbChannelCommand.getCooldown()).lastExecution(dbChannelCommand.getLastExecution()).messages(getMessages(dbChannelCommand)).build();

	}

}
