package fr.damnardev.twitch.bot.domain.model;

import lombok.Builder;

@Builder
public record Message(ChannelInfo channel, String value) {

}
