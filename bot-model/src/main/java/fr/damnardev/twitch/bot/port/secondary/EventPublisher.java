package fr.damnardev.twitch.bot.port.secondary;

import fr.damnardev.twitch.bot.model.event.Event;

public interface EventPublisher {

	<T extends Event> void publish(T event);

}
