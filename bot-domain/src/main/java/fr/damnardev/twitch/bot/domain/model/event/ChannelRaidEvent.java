package fr.damnardev.twitch.bot.domain.model.event;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Value
public class ChannelRaidEvent extends Event {

	@Builder
	public ChannelRaidEvent(String error) {
		super(error);
	}

}
