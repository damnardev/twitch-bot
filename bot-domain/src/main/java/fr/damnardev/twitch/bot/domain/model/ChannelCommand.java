package fr.damnardev.twitch.bot.domain.model;

import lombok.Builder;

@Builder
public record ChannelCommand(User channel, String name) {

}
