package fr.damnardev.twitch.bot.core.service.command;


import fr.damnardev.twitch.bot.DomainService;
import fr.damnardev.twitch.bot.model.Channel;
import fr.damnardev.twitch.bot.model.Command;
import fr.damnardev.twitch.bot.model.CommandType;
import fr.damnardev.twitch.bot.model.Message;
import fr.damnardev.twitch.bot.model.form.ChannelMessageEventForm;
import fr.damnardev.twitch.bot.port.secondary.MessageRepository;
import fr.damnardev.twitch.bot.port.secondary.SaintRepository;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class SaintInterpreter implements CommandInterpreter {

	private final MessageRepository messageRepository;

	private final SaintRepository saintRepository;

	@Override
	public void interpret(Channel channel, Command command, ChannelMessageEventForm form) {
		var value = this.saintRepository.find();
		if (value.isPresent()) {
			var message = Message.builder().channelId(channel.id()).channelName(channel.name()).content(value.get()).build();
			this.messageRepository.sendMessage(message);
		}
	}

	public CommandType getCommandTypeInterpreter() {
		return CommandType.SAINT;
	}

}
