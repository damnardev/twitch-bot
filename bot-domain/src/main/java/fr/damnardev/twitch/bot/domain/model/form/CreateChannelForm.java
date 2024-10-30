package fr.damnardev.twitch.bot.domain.model.form;

import lombok.Builder;

@Builder
public record CreateChannelForm(String name) {
}
