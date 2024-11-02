package fr.damnardev.twitch.bot.domain.model.event;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Value
public class ErrorEvent extends Event {

	Exception exception;

	@Builder
	public ErrorEvent(Exception exception, String error) {
		super(error);
		this.exception = exception;
	}

}
