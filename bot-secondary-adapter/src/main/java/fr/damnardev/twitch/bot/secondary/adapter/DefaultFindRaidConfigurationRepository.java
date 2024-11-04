package fr.damnardev.twitch.bot.secondary.adapter;

import java.util.Optional;

import fr.damnardev.twitch.bot.database.entity.DbRaidConfiguration;
import fr.damnardev.twitch.bot.database.repository.DbRaidConfigurationRepository;
import fr.damnardev.twitch.bot.domain.model.RaidConfiguration;
import fr.damnardev.twitch.bot.domain.port.secondary.FindRaidConfigurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class DefaultFindRaidConfigurationRepository implements FindRaidConfigurationRepository {

	private final DbRaidConfigurationRepository dbRaidConfigurationRepository;

	@Override
	public Optional<RaidConfiguration> findByChannelName(String name) {
		return this.dbRaidConfigurationRepository.findByChannelName(name).map(this::toModel);
	}

	private RaidConfiguration toModel(DbRaidConfiguration dbChannelRaid) {
		return RaidConfiguration.builder().id(dbChannelRaid.getId()).raidMessageEnabled(dbChannelRaid.isRaidMessageEnabled()).twitchShoutoutEnabled(dbChannelRaid.isTwitchShoutoutEnabled()).wizebotShoutoutEnabled(dbChannelRaid.isWizebotShoutoutEnabled()).messages(dbChannelRaid.getMessages()).build();
	}

}
