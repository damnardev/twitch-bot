package fr.damnardev.twitch.bot.domain.model;

import java.util.List;

import lombok.Builder;

@Builder(toBuilder = true)
public record RaidConfiguration(Long id, boolean twitchShoutoutEnabled,
		boolean wizebotShoutoutEnabled, boolean raidMessageEnabled,
		List<String> messages) {
}
