package fr.damnardev.twitch.bot.domain.service.command;


import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.model.Command;
import fr.damnardev.twitch.bot.domain.model.Message;
import fr.damnardev.twitch.bot.domain.model.form.ChannelMessageEventForm;
import fr.damnardev.twitch.bot.domain.port.secondary.MessageRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.SaintRepository;
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

	@Override
	public boolean canInterpret(Channel channel, Command command, ChannelMessageEventForm form) {
		return command.name().equalsIgnoreCase("saints");
	}

}
