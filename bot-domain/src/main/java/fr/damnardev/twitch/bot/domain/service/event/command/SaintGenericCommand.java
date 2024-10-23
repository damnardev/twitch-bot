package fr.damnardev.twitch.bot.domain.service.event.command;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.model.event.MessageEvent;
import fr.damnardev.twitch.bot.domain.port.secondary.ISaintRepository;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class SaintGenericCommand implements IGenericCommand {

	private final ISaintRepository saintRepository;

	@Override
	public String name() {
		return "!saints";
	}

	@Override
	public String process(MessageEvent event) {
		return this.saintRepository.find().value();
	}

}
