package fr.damnardev.twitch.bot.domain.model.form;

import lombok.Builder;

@Builder
public record DeleteRaidConfigurationMessageForm(Long id, String name, String message) {
}
