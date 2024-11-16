package fr.damnardev.twitch.bot.domain.port.secondary.raid;

import java.util.List;
import java.util.Optional;

import fr.damnardev.twitch.bot.domain.model.RaidConfiguration;

public interface FindRaidConfigurationRepository {

	Optional<RaidConfiguration> findByChannelName(String name);

	List<RaidConfiguration> findAll();

}
