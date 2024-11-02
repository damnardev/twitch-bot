package fr.damnardev.twitch.bot.domain.model.event;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Value
public class ApplicationStartedEvent extends Event {

	@Builder
	public ApplicationStartedEvent(String error) {
		super(error);
	}

}
