package fr.damnardev.twitch.bot.domain.model.event;

import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
public abstract class Event<T> {

	@EqualsAndHashCode.Exclude
	private final UUID id;

	private final T value;

	protected Event() {
		this.id = UUID.randomUUID();
		this.value = null;
	}

	protected Event(T value) {
		this.id = UUID.randomUUID();
		this.value = value;
	}

	public abstract boolean hasError();

}
