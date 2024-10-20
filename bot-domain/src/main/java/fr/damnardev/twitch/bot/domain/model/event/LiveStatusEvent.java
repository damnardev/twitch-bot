package fr.damnardev.twitch.bot.domain.model.event;

import lombok.Builder;

@Builder
public record LiveStatusEvent(String broadcasterId, String broadcasterIdUserName, boolean online) {

}
