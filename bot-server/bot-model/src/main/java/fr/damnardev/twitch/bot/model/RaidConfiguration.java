package fr.damnardev.twitch.bot.model;

import java.util.List;

import lombok.Builder;

@Builder(toBuilder = true)
public record RaidConfiguration(Long channelId, String channelName,
		boolean twitchShoutoutEnabled,
		boolean wizebotShoutoutEnabled, boolean raidMessageEnabled,
		List<String> messages) {
}
