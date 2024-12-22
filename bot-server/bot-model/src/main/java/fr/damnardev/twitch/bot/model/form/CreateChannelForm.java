package fr.damnardev.twitch.bot.model.form;

import lombok.Builder;

@Builder
public record CreateChannelForm(String name) {
}
