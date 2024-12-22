package fr.damnardev.twitch.bot.port.secondary.raid;

import java.util.List;
import java.util.Optional;

import fr.damnardev.twitch.bot.model.Channel;
import fr.damnardev.twitch.bot.model.RaidConfiguration;

public interface FindRaidConfigurationRepository {

	Optional<RaidConfiguration> findByChannel(Channel channel);

	List<RaidConfiguration> findAll();

}
