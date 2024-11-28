package fr.damnardev.twitch.bot.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record SuggestGame(String viewer, String game) {
}
