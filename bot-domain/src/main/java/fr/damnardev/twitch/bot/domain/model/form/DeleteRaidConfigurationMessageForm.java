package fr.damnardev.twitch.bot.domain.model.form;

import lombok.Builder;

@Builder
public record DeleteRaidConfigurationMessageForm(Long channelId, String channelName, String message) {
}
