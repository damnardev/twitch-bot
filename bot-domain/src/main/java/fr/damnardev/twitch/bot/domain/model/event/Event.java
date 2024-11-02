package fr.damnardev.twitch.bot.domain.model.event;

import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public abstract class Event {

	@EqualsAndHashCode.Exclude
	@Getter
	private final UUID id;

	@EqualsAndHashCode.Include
	@Getter
	private final String error;

	protected Event(String error) {
		this.id = UUID.randomUUID();
		this.error = error;
	}

	public boolean hasError() {
		return this.error != null;
	}

}
