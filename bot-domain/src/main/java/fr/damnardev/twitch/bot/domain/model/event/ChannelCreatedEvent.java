package fr.damnardev.twitch.bot.domain.model.event;

import fr.damnardev.twitch.bot.domain.model.Channel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Value
public class ChannelCreatedEvent extends Event<Channel> {

	@Builder
	public ChannelCreatedEvent(Channel channel) {
		super(channel);
	}

	@Override
	public boolean hasError() {
		return false;
	}

}
