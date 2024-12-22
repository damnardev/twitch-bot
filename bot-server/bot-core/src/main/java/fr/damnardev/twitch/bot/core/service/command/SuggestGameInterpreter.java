package fr.damnardev.twitch.bot.core.service.command;

import fr.damnardev.twitch.bot.DomainService;
import fr.damnardev.twitch.bot.exception.FatalException;
import fr.damnardev.twitch.bot.model.Channel;
import fr.damnardev.twitch.bot.model.Command;
import fr.damnardev.twitch.bot.model.CommandType;
import fr.damnardev.twitch.bot.model.Message;
import fr.damnardev.twitch.bot.model.SuggestGame;
import fr.damnardev.twitch.bot.model.form.ChannelMessageEventForm;
import fr.damnardev.twitch.bot.port.primary.RandomService;
import fr.damnardev.twitch.bot.port.secondary.MessageRepository;
import fr.damnardev.twitch.bot.port.secondary.SuggestGameRepository;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class SuggestGameInterpreter implements CommandInterpreter {

	private final SuggestGameRepository suggestGameRepository;

	private final MessageRepository messageRepository;

	private final RandomService randomService;

	@Override
	public void interpret(Channel channel, Command command, ChannelMessageEventForm form) {
		var suggestGame = SuggestGame.builder().viewer(form.sender()).game(form.message()).build();
		if (form.message() == null || form.message().isBlank()) {
			var content = String.format("/me usage: !%s nom_du_jeu", command.name());
			var message = Message.builder().channelId(channel.id()).channelName(channel.name()).content(content).build();
			this.messageRepository.sendMessage(message);
			throw new FatalException("No game suggested");
		}
		var result = this.suggestGameRepository.suggest(channel, suggestGame);
		if (result) {
			var value = this.randomService.getRandom(command.messages());
			var content = String.format("%s [⏰ %d s]", value, command.cooldown());
			var message = Message.builder().channelId(channel.id()).channelName(channel.name()).content(content).build();
			this.messageRepository.sendMessage(message);
		}
	}

	@Override
	public CommandType getCommandTypeInterpreter() {
		return CommandType.SUGGEST_GAME;
	}

}
