package fr.damnardev.twitch.bot.domain.model;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record CommandConfiguration(Long id, String name, boolean enabled, OffsetDateTime lastExecution,
                                   long cooldown) {

    public boolean canProcess() {
        return OffsetDateTime.now()
                             .plusSeconds(cooldown)
                             .isAfter(lastExecution);
    }

}
