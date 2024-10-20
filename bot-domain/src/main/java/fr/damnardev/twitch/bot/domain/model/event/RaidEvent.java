package fr.damnardev.twitch.bot.domain.model.event;

import lombok.Builder;

@Builder
public record RaidEvent(String fromUserId, String fromUserName, String toUserId, String toUserName) {

}
