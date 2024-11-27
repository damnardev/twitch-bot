package fr.damnardev.twitch.bot.port.primary;

import java.time.OffsetDateTime;
import java.time.ZoneId;

public interface DateService {

	OffsetDateTime now();

	OffsetDateTime now(ZoneId zoneId);

}
