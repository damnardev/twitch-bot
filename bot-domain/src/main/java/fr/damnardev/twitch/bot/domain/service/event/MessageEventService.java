package fr.damnardev.twitch.bot.domain.service.event;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.model.ChannelCommand;
import fr.damnardev.twitch.bot.domain.model.Message;
import fr.damnardev.twitch.bot.domain.model.User;
import fr.damnardev.twitch.bot.domain.model.event.MessageEvent;
import fr.damnardev.twitch.bot.domain.port.primary.event.IMessageEventService;
import fr.damnardev.twitch.bot.domain.port.secondary.IFindChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.IFindCommandConfigurationRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.IMessageRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.ISaveCommandConfigurationRepository;
import fr.damnardev.twitch.bot.domain.service.event.command.IGenericCommand;

@DomainService
public class MessageEventService implements IMessageEventService {

	private final IFindChannelRepository findChannelRepository;

	private final IFindCommandConfigurationRepository findCommandConfigurationRepository;

	private final ISaveCommandConfigurationRepository saveCommandConfigurationRepository;

	private final IMessageRepository messageRepository;

	private final Map<String, IGenericCommand> commands;

	public MessageEventService(IFindChannelRepository findChannelRepository, IFindCommandConfigurationRepository findCommandConfigurationRepository, ISaveCommandConfigurationRepository saveCommandConfigurationRepository, IMessageRepository messageRepository, List<IGenericCommand> commands) {
		this.findChannelRepository = findChannelRepository;
		this.findCommandConfigurationRepository = findCommandConfigurationRepository;
		this.saveCommandConfigurationRepository = saveCommandConfigurationRepository;
		this.messageRepository = messageRepository;
		this.commands = commands.stream().collect(Collectors.toMap(IGenericCommand::name, Function.identity()));
	}

	@Override
	public void process(MessageEvent event) {
		var channel = this.findChannelRepository.find(event.user()).orElse(null);
		if (channel == null || channel.isDisabled() || !event.message().startsWith("!")) {
			return;
		}
		var user = channel.user();
		var command = ChannelCommand.builder().channel(user).name(event.message()).build();
		var commandConfiguration = this.findCommandConfigurationRepository.find(command);
		if (!commandConfiguration.enabled() || !commandConfiguration.canProcess()) {
			return;
		}
		var result = this.commands.get(command.name()).process(event);
		var message = toMessage(user, result);
		this.saveCommandConfigurationRepository.updateLastExecution(commandConfiguration);
		this.messageRepository.send(message);
	}

	private Message toMessage(User user, String message) {
		return Message.builder().channel(user).value(message).build();
	}

}
