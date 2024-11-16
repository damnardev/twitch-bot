package fr.damnardev.twitch.bot.domain.port.primary;

import fr.damnardev.twitch.bot.domain.exception.FatalException;

public interface EventService<M> {

	default void process(M event) {
		throw new FatalException("Not implemented");
	}

	default void process() {
		throw new FatalException("Not implemented");
	}

}
