package fr.damnardev.twitch.bot.model.form;

import lombok.Builder;

@Builder
public record DeleteRaidConfigurationMessageForm(Long channelId, String channelName, String message) {
}
