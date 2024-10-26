package fr.damnardev.twitch.bot.domain.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record User(Long id, String name) {

}
