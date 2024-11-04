package fr.damnardev.twitch.bot.domain.port.secondary;

import java.util.Optional;

import fr.damnardev.twitch.bot.domain.model.RaidConfiguration;

public interface FindRaidConfigurationRepository {

	Optional<RaidConfiguration> findByChannelName(String name);

}
