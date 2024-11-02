package fr.damnardev.twitch.bot.domain.model.event;

import fr.damnardev.twitch.bot.domain.model.Channel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Value
public class ChannelUpdatedEvent extends Event {

	Channel channel;

	@Builder
	public ChannelUpdatedEvent(Channel channel, String error) {
		super(error);
		this.channel = channel;
	}

}
