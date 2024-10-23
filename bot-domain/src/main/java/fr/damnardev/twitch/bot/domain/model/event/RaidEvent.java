package fr.damnardev.twitch.bot.domain.model.event;

import fr.damnardev.twitch.bot.domain.model.User;
import lombok.Builder;

@Builder
public record RaidEvent(User raider, User channel) {

}
