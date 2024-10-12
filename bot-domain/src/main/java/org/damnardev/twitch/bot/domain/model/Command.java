package org.damnardev.twitch.bot.domain.model;

import lombok.Builder;

@Builder
public record Command(ChannelInfo channel, String name) {

}
