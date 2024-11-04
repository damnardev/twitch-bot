package fr.damnardev.twitch.bot.domain.model.form;

import lombok.Builder;

@Builder
public record ChannelRaidEventForm(Long id, String name, Long raiderId,
		String raiderName) {
}
