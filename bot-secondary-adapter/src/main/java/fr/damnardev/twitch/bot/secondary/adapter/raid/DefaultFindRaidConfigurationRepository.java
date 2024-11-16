package fr.damnardev.twitch.bot.secondary.adapter.raid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.damnardev.twitch.bot.database.entity.DbRaidConfiguration;
import fr.damnardev.twitch.bot.database.repository.DbRaidConfigurationRepository;
import fr.damnardev.twitch.bot.domain.model.RaidConfiguration;
import fr.damnardev.twitch.bot.domain.port.secondary.raid.FindRaidConfigurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class DefaultFindRaidConfigurationRepository implements FindRaidConfigurationRepository {

	private final DbRaidConfigurationRepository dbRaidConfigurationRepository;

	private static @NotNull List<String> getMessages(DbRaidConfiguration dbChannelRaid) {
		if (dbChannelRaid.getMessages() != null) {
			return new ArrayList<>(dbChannelRaid.getMessages());
		}
		return new ArrayList<>();
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<RaidConfiguration> findByChannelName(String name) {
		return this.dbRaidConfigurationRepository.findByChannelName(name).map(this::toModel);
	}

	@Override
	@Transactional(readOnly = true)
	public List<RaidConfiguration> findAll() {
		return this.dbRaidConfigurationRepository.findAll().stream().map(this::toModel).toList();
	}

	private RaidConfiguration toModel(DbRaidConfiguration dbChannelRaid) {
		return RaidConfiguration.builder().channelId(dbChannelRaid.getId()).channelName(dbChannelRaid.getChannel().getName()).raidMessageEnabled(dbChannelRaid.isRaidMessageEnabled()).twitchShoutoutEnabled(dbChannelRaid.isTwitchShoutoutEnabled()).wizebotShoutoutEnabled(dbChannelRaid.isWizebotShoutoutEnabled()).messages(getMessages(dbChannelRaid)).build();
	}

}
