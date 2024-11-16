package fr.damnardev.twitch.bot.secondary.adapter.raid;

import fr.damnardev.twitch.bot.database.repository.DbRaidConfigurationRepository;
import fr.damnardev.twitch.bot.domain.model.RaidConfiguration;
import fr.damnardev.twitch.bot.domain.port.secondary.raid.UpdateRaidConfigurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class DefaultUpdateRaidConfigurationRepository implements UpdateRaidConfigurationRepository {

	private final DbRaidConfigurationRepository dbRaidConfigurationRepository;

	@Override
	@Transactional
	public void update(RaidConfiguration raidConfiguration) {
		log.info("Updating raid configuration {}", raidConfiguration.channelName());
		this.dbRaidConfigurationRepository.findByChannelName(raidConfiguration.channelName()).ifPresent((dbRaidConfiguration) -> {
			dbRaidConfiguration.setRaidMessageEnabled(raidConfiguration.raidMessageEnabled());
			dbRaidConfiguration.setTwitchShoutoutEnabled(raidConfiguration.twitchShoutoutEnabled());
			dbRaidConfiguration.setWizebotShoutoutEnabled(raidConfiguration.wizebotShoutoutEnabled());
			dbRaidConfiguration.getMessages().clear();
			dbRaidConfiguration.getMessages().addAll(raidConfiguration.messages());
			this.dbRaidConfigurationRepository.save(dbRaidConfiguration);
		});
		log.info("Updated raid configuration {}", raidConfiguration.channelName());
	}

}
