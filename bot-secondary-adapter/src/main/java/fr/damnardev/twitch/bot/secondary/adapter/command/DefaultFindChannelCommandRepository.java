package fr.damnardev.twitch.bot.secondary.adapter.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import fr.damnardev.twitch.bot.database.entity.DbChannelCommand;
import fr.damnardev.twitch.bot.database.repository.DbChannelCommandRepository;
import fr.damnardev.twitch.bot.domain.model.Command;
import fr.damnardev.twitch.bot.domain.port.secondary.command.FindChannelCommandRepository;
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
	public Map<String, Command> findByChannelName(String name) {
		return this.dbChannelCommandRepository.findByChannelName(name).stream().map(this::toModel).collect(java.util.stream.Collectors.toMap(Command::name, Function.identity()));
	}

	private Command toModel(DbChannelCommand dbChannelCommand) {
		return Command.builder().channelId(dbChannelCommand.getId()).channelName(dbChannelCommand.getChannel().getName()).name(dbChannelCommand.getName()).enabled(dbChannelCommand.isEnabled()).cooldown(dbChannelCommand.getCooldown()).lastExecution(dbChannelCommand.getLastExecution()).messages(getMessages(dbChannelCommand)).build();

	}

}
