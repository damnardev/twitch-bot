package fr.damnardev.twitch.bot.core.service;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import fr.damnardev.twitch.bot.DomainService;
import fr.damnardev.twitch.bot.core.service.command.CommandInterpreter;
import fr.damnardev.twitch.bot.exception.BusinessException;
import fr.damnardev.twitch.bot.model.Channel;
import fr.damnardev.twitch.bot.model.Command;
import fr.damnardev.twitch.bot.model.CommandType;
import fr.damnardev.twitch.bot.model.form.ChannelMessageEventForm;
import fr.damnardev.twitch.bot.port.primary.ChannelMessageEventService;
import fr.damnardev.twitch.bot.port.primary.DateService;
import fr.damnardev.twitch.bot.port.secondary.channel.FindChannelRepository;
import fr.damnardev.twitch.bot.port.secondary.command.FindChannelCommandRepository;
import fr.damnardev.twitch.bot.port.secondary.command.UpdateChannelCommandRepository;

@DomainService
public class DefaultChannelMessageEventService implements ChannelMessageEventService {

	private final DefaultTryService tryService;

	private final FindChannelRepository findChannelRepository;

	private final FindChannelCommandRepository channelCommandRepository;

	private final UpdateChannelCommandRepository updateChannelCommandRepository;

	private final Map<CommandType, CommandInterpreter> commandInterpreters;

	private final DateService dateService;

	public DefaultChannelMessageEventService(DefaultTryService tryService, FindChannelRepository findChannelRepository, FindChannelCommandRepository channelCommandRepository, UpdateChannelCommandRepository updateChannelCommandRepository, List<CommandInterpreter> commandInterpreters, DateService dateService) {
		this.tryService = tryService;
		this.findChannelRepository = findChannelRepository;
		this.channelCommandRepository = channelCommandRepository;
		this.updateChannelCommandRepository = updateChannelCommandRepository;
		this.commandInterpreters = (commandInterpreters != null) ? commandInterpreters.stream().collect(Collectors.toMap(CommandInterpreter::getCommandTypeInterpreter, Function.identity())) : new HashMap<>();
		this.dateService = dateService;
	}

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
			var split = form.message().substring(1).split(" ", 2);
			var commandName = split[0];
			var parameter = (split.length == 2) ? split[1] : "";
			form = form.toBuilder().message(parameter).build();
			var optionalCommand = this.channelCommandRepository.findByChannelAndName(channel, commandName);
			if (optionalCommand.isEmpty()) {
				return;
			}
			var command = optionalCommand.get();
			var now = this.dateService.now();
			if (command.lastExecution() != null && command.lastExecution().plusSeconds(command.cooldown()).isAfter(now)) {
				return;
			}
			this.process(channel, command, form, now);
		}
	}

	private void process(Channel channel, Command command, ChannelMessageEventForm form, OffsetDateTime now) {
		var commandInterpreter = this.commandInterpreters.get(command.type());
		if (commandInterpreter != null) {
			var updatedCommand = command.toBuilder().lastExecution(now).build();
			commandInterpreter.interpret(channel, command, form);
			this.updateChannelCommandRepository.update(updatedCommand);
		}
	}

}
