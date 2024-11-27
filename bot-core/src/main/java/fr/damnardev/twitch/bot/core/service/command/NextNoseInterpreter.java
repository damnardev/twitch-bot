package fr.damnardev.twitch.bot.core.service.command;

import fr.damnardev.twitch.bot.DomainService;
import fr.damnardev.twitch.bot.model.CommandType;
import fr.damnardev.twitch.bot.port.primary.DateService;
import fr.damnardev.twitch.bot.port.secondary.MessageRepository;

@DomainService
public class NextNoseInterpreter extends NoseInterpreter {

	public NextNoseInterpreter(MessageRepository messageRepository, DateService dateService) {
		super(messageRepository, dateService);
	}

	@Override
	protected boolean isForward() {
		return true;
	}

	@Override
	public CommandType getCommandTypeInterpreter() {
		return CommandType.NEXT_NOSE;
	}

}
