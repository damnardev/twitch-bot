package fr.damnardev.twitch.bot.domain.service;

import java.time.OffsetDateTime;
import java.util.List;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.exception.BusinessException;
import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.model.Command;
import fr.damnardev.twitch.bot.domain.model.form.ChannelMessageEventForm;
import fr.damnardev.twitch.bot.domain.port.primary.ChannelMessageEventService;
import fr.damnardev.twitch.bot.domain.port.secondary.channel.FindChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.command.FindChannelCommandRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.command.UpdateChannelCommandRepository;
import fr.damnardev.twitch.bot.domain.service.command.CommandInterpreter;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class DefaultChannelMessageEventService implements ChannelMessageEventService {

	private final DefaultTryService tryService;

	private final FindChannelRepository findChannelRepository;

	private final FindChannelCommandRepository channelCommandRepository;

	private final UpdateChannelCommandRepository updateChannelCommandRepository;

	private final List<CommandInterpreter> commandInterpreters;

	@Override
	public void process(ChannelMessageEventForm form) {
		this.tryService.doTry(this::doInternal, form);
	}

	private void doInternal(ChannelMessageEventForm form) {
		var optionalChannel = this.findChannelRepository.findByName(form.channelName());
		if (optionalChannel.isEmpty()) {
			throw new BusinessException("Channel not found");
		}
		var channel = optionalChannel.get();
		if (form.message().startsWith("!")) {
			form = form.toBuilder().message(form.message().substring(1)).build();
			var command = this.channelCommandRepository.findByChannel(channel).get(form.message());
			if (command == null || (command.lastExecution() != null && command.lastExecution().plusSeconds(command.cooldown()).isAfter(OffsetDateTime.now()))) {
				return;
			}
			this.process(channel, command, form);
		}
	}

	private void process(Channel channel, Command command, ChannelMessageEventForm form) {
		var commandInterpreter = this.commandInterpreters.stream().filter((interpreter) -> interpreter.canInterpret(channel, command, form)).findFirst();
		if (commandInterpreter.isPresent()) {
			var updatedCommand = command.toBuilder().lastExecution(OffsetDateTime.now()).build();
			commandInterpreter.get().interpret(channel, command, form);
			this.updateChannelCommandRepository.update(updatedCommand);
		}
	}

}
