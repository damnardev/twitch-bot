package fr.damnardev.twitch.bot.domain.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record ChannelInfo(User user, boolean enabled, boolean online) {

	public boolean isDisabled() {
		return !this.enabled || !this.online;
	}

}
