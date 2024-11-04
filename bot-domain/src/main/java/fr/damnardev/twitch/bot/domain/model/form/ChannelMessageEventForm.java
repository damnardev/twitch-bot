package fr.damnardev.twitch.bot.domain.model.form;

import lombok.Builder;

@Builder
public record ChannelMessageEventForm(Long id, String name, String sender,
		String message) {
}
