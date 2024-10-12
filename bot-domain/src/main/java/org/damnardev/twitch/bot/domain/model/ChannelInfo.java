package org.damnardev.twitch.bot.domain.model;

import lombok.Builder;


@Builder
public record ChannelInfo(Long id, String name, boolean enabled, boolean online) {

    public boolean canProcess() {
        return enabled;
    }

}
