package fr.damnardev.twitch.bot.domain.model;

import java.time.OffsetDateTime;

import lombok.Builder;

@Builder
public record ChannelCommandConfiguration(Long id, String name, boolean enabled,
		OffsetDateTime lastExecution, long cooldown) {

	public boolean canProcess() {
		return OffsetDateTime.now().plusSeconds(this.cooldown).isAfter(this.lastExecution);
	}

}
