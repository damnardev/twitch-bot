package fr.damnardev.twitch.bot.domain.model;

import java.util.List;

import lombok.Builder;

@Builder
public record ChannelRaidConfiguration(boolean twitchShoutoutEnabled,
		boolean wizebotShoutoutEnabled, boolean raidMessageEnabled,
		List<String> messages) {

}
