package fr.damnardev.twitch.bot.domain.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record Shoutout(Long channelId, String channelName, Long raiderId,
		String raiderName) {
}
