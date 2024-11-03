package fr.damnardev.twitch.bot.domain.model.form;

import lombok.Builder;

@Builder
public record UpdateChannelOnlineForm(Long id, String name, boolean online) {
}
