package fr.damnardev.twitch.bot.domain.port.secondary;

import fr.damnardev.twitch.bot.domain.model.event.Event;

public interface EventPublisher {

	<T extends Event> void publish(T event);

}
