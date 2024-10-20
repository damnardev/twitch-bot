package fr.damnardev.twitch.bot.domain.service.command;

import lombok.RequiredArgsConstructor;
import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.model.event.MessageEvent;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@DomainService
@RequiredArgsConstructor
public class PrevNoseGenericCommand implements IGenericCommand {

    private final ZoneId zoneId = ZoneId.of("Europe/Paris");

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public String name() {
        return "!prevnez";
    }

    @Override
    public String process(MessageEvent event) {
        var now = OffsetDateTime.now(zoneId)
                                .truncatedTo(ChronoUnit.MINUTES);
        var next = now;
        if (now.getHour() > now.getMinute()) {
            next = next.plusHours(-1);

        }
        next = next.withMinute(next.getHour());
        var diff = ChronoUnit.MINUTES.between(next, now);
        var formattedDateTime = next.format(formatter);
        return String.format("Le nez précédent remonte %d minutes %s [60sec] %n", diff, formattedDateTime);
    }

}
