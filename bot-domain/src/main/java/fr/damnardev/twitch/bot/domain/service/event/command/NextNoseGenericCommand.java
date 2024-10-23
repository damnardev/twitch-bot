package fr.damnardev.twitch.bot.domain.service.event.command;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.model.event.MessageEvent;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class NextNoseGenericCommand implements IGenericCommand {

	public static final String STRING = "Le prochain nez sera dans %d minutes %s%n";

	@Override
	public String name() {
		return "!nextnez";
	}

	@Override
	public String process(MessageEvent event) {
		var now = OffsetDateTime.now(zoneId).truncatedTo(ChronoUnit.MINUTES);
		var next = now;
		if (now.getMinute() > now.getHour()) {
			next = next.plusHours(1);
		}
		next = next.withMinute(next.getHour());
		var diff = ChronoUnit.MINUTES.between(now, next);
		var formattedDateTime = next.format(formatter);
		return String.format(STRING, diff, formattedDateTime);
	}

}
