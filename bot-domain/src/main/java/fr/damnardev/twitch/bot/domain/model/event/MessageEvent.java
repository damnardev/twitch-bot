package fr.damnardev.twitch.bot.domain.model.event;

import lombok.Builder;

@Builder
public record MessageEvent(String fromUserName, String toUserName, String message) {

}
