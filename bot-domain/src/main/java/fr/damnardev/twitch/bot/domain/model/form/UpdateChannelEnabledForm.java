package fr.damnardev.twitch.bot.domain.model.form;

import lombok.Builder;

@Builder
public record UpdateChannelEnabledForm(Long id, String name, boolean enabled) {
}
