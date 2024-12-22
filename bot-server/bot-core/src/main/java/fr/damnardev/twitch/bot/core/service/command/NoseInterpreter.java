package fr.damnardev.twitch.bot.core.service.command;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import fr.damnardev.twitch.bot.model.Channel;
import fr.damnardev.twitch.bot.model.Command;
import fr.damnardev.twitch.bot.model.Message;
import fr.damnardev.twitch.bot.model.form.ChannelMessageEventForm;
import fr.damnardev.twitch.bot.port.primary.DateService;
import fr.damnardev.twitch.bot.port.secondary.MessageRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class NoseInterpreter implements CommandInterpreter {

	private static final String PREV_NOSE = "Le nez précédent remonte à %d minutes %s [⏰ %d s]";

	private static final String NEXT_NOSE = "Le prochain nez sera dans %d minutes %s [⏰ %d s]";

	private static final ZoneId zoneId = ZoneId.of("Europe/Paris");

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	private final MessageRepository messageRepository;

	private final DateService dateService;

	@Override
	public void interpret(Channel channel, Command command, ChannelMessageEventForm form) {
		var now = this.dateService.now(zoneId).truncatedTo(ChronoUnit.MINUTES);
		var value = now;

		var isForward = isForward();
		if (isForward) {
			if (now.getMinute() > now.getHour()) {
				value = value.plusHours(1);
			}
		}
		else {
			if (now.getHour() > now.getMinute()) {
				value = value.minusHours(1);
			}
		}

		value = value.withMinute(value.getHour());
		var diff = isForward ? ChronoUnit.MINUTES.between(now, value) : ChronoUnit.MINUTES.between(value, now);
		var formattedDateTime = value.format(formatter);
		var string = isForward ? NEXT_NOSE : PREV_NOSE;
		var content = String.format(string, diff, formattedDateTime, command.cooldown());
		var message = Message.builder().channelId(channel.id()).channelName(channel.name()).content(content).build();
		this.messageRepository.sendMessage(message);
	}

	protected abstract boolean isForward();

}
