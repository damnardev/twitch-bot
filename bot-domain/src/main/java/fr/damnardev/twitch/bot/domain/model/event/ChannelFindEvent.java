package fr.damnardev.twitch.bot.domain.model.event;

import java.util.List;

import fr.damnardev.twitch.bot.domain.model.Channel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Value
public class ChannelFindEvent extends Event {

	List<Channel> channels;

	@Builder
	public ChannelFindEvent(List<Channel> channels, String error) {
		super(error);
		this.channels = channels;
	}

}
