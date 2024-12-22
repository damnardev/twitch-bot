package fr.damnardev.twitch.bot.model;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.Builder;

@Builder(toBuilder = true)
public record Command(Long channelId, String channelName, String name, boolean enabled,
		long cooldown, OffsetDateTime lastExecution, List<String> messages,
		CommandType type) {
}
