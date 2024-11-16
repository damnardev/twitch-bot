package fr.damnardev.twitch.bot.domain.port.secondary.raid;

import fr.damnardev.twitch.bot.domain.model.RaidConfiguration;

public interface UpdateRaidConfigurationRepository {

	void update(RaidConfiguration raidConfiguration);

}
