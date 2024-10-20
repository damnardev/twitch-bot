package fr.damnardev.twitch.bot.domain.model;

import lombok.Builder;

import java.util.List;

@Builder
public record ChannelRaidConfiguration(boolean twitchShoutoutEnabled, boolean wizebotShoutoutEnabled,
                                       boolean raidMessageEnabled, List<String> messages) {

}
