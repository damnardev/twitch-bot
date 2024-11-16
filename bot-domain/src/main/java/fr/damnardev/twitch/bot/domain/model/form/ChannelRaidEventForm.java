package fr.damnardev.twitch.bot.domain.model.form;

import lombok.Builder;

@Builder
public record ChannelRaidEventForm(Long channelId, String channelName, Long raiderId,
		String raiderName) {
}
