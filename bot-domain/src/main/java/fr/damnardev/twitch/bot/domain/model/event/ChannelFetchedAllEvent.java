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
public class ChannelFetchedAllEvent extends Event<List<Channel>> {

	@Builder
	public ChannelFetchedAllEvent(List<Channel> channels) {
		super(channels);
	}

	@Override
	public boolean hasError() {
		return false;
	}

}
