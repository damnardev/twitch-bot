package fr.damnardev.twitch.bot.domain.service.event.command;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.model.event.MessageEvent;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@DomainService
@RequiredArgsConstructor
public class PrevNoseGenericCommand implements IGenericCommand {

    public static final String STRING = "Le nez précédent remonte %d minutes %s%n";

    @Override
    public String name() {
        return "!prevnez";
    }

    @Override
    public String process(MessageEvent event) {
        var now = OffsetDateTime.now(zoneId).truncatedTo(ChronoUnit.MINUTES);
        var next = now;
        if (now.getHour() > now.getMinute()) {
            next = next.plusHours(-1);
        }
        next = next.withMinute(next.getHour());
        var diff = ChronoUnit.MINUTES.between(next, now);
        var formattedDateTime = next.format(formatter);
        return String.format(STRING, diff, formattedDateTime);
    }

}
