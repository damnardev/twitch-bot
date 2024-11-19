package fr.damnardev.twitch.bot.domain.model.form;

import lombok.Builder;

@Builder
public record UpdateRaidConfigurationForm(Long channelId, String channelName,
		Boolean wizebotShoutoutEnabled, Boolean twitchShoutoutEnabled,
		Boolean raidMessageEnabled) {
}
