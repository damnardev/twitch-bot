package fr.damnardev.twitch.bot.model.event;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Value
public class ErrorEvent extends Event<Void> {

	Exception exception;

	@Builder
	public ErrorEvent(Exception exception) {
		super();
		this.exception = exception;
	}

	@Override
	public boolean hasError() {
		return true;
	}

}
