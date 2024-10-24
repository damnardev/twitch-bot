package fr.damnardev.twitch.bot.primary.javafx.wrapper;

import fr.damnardev.twitch.bot.domain.model.ChannelInfo;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ChannelInfoWrapper {

	private ChannelInfo channelInfo;

	public Long getId() {
		return this.channelInfo.user().id();
	}

	public String getName() {
		return this.channelInfo.user().name();
	}

	public Boolean getEnabled() {
		return this.channelInfo.enabled();
	}

}
