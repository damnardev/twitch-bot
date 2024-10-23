package fr.damnardev.twitch.bot.domain.service.event.command;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import fr.damnardev.twitch.bot.domain.model.event.MessageEvent;

public interface IGenericCommand {

	ZoneId zoneId = ZoneId.of("Europe/Paris");

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	String name();

	String process(MessageEvent event);

}
