package fr.damnardev.twitch.bot.model.form;

import lombok.Builder;

@Builder
public record DeleteChannelForm(Long id, String name) {
}
