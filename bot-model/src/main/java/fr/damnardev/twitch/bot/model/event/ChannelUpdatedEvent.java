package fr.damnardev.twitch.bot.model.event;

import fr.damnardev.twitch.bot.model.Channel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Value
public class ChannelUpdatedEvent extends Event<Channel> {

	@Builder
	public ChannelUpdatedEvent(Channel channel) {
		super(channel);
	}

	@Override
	public boolean hasError() {
		return false;
	}

}
