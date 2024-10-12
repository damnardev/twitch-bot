package org.damnardev.twitch.bot.domain.service;

import org.damnardev.twitch.bot.domain.DomainService;
import org.damnardev.twitch.bot.domain.model.ChannelInfo;
import org.damnardev.twitch.bot.domain.model.Command;
import org.damnardev.twitch.bot.domain.model.Message;
import org.damnardev.twitch.bot.domain.model.event.MessageEvent;
import org.damnardev.twitch.bot.domain.port.primary.IMessageService;
import org.damnardev.twitch.bot.domain.port.secondary.IChannelRepository;
import org.damnardev.twitch.bot.domain.port.secondary.ICommandConfigurationRepository;
import org.damnardev.twitch.bot.domain.port.secondary.IMessageRepository;
import org.damnardev.twitch.bot.domain.service.command.IGenericCommand;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@DomainService
public class MessageService implements IMessageService {

    private final IChannelRepository channelRepository;

    private final ICommandConfigurationRepository commandConfigurationRepository;

    private final IMessageRepository messageRepository;

    private final Map<String, IGenericCommand> commands;

    public MessageService(IChannelRepository channelRepository, ICommandConfigurationRepository commandConfigurationRepository, IMessageRepository messageRepository, List<IGenericCommand> commands) {
        this.channelRepository = channelRepository;
        this.commandConfigurationRepository = commandConfigurationRepository;
        this.messageRepository = messageRepository;
        this.commands = commands.stream()
                                .collect(Collectors.toMap(IGenericCommand::name, Function.identity()));
    }

    @Override
    public void process(MessageEvent event) {
        var channelName = event.toUserName();
        var channel = channelRepository.find(channelName);

        if (channel == null || !channel.canProcess() || !event.message()
                                                              .startsWith("!")) {
            return;
        }

        var command = Command.builder()
                             .channel(channel)
                             .name(event.message())
                             .build();
        var commandConfiguration = commandConfigurationRepository.find(command);

        if (!commandConfiguration.enabled() || !commandConfiguration.canProcess()) {
            return;
        }

        var result = commands.get(command.name())
                             .process(event);
        var message = toMessage(channel, result);
        messageRepository.send(message);
        commandConfigurationRepository.updateLastExecution(commandConfiguration);
    }

    private Message toMessage(ChannelInfo channel, String message) {
        return Message.builder()
                      .channel(channel)
                      .value(message)
                      .build();
    }

}
