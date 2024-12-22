package fr.damnardev.twitch.bot.database.repository;

import java.util.Collections;

import fr.damnardev.twitch.bot.database.entity.DbChannel;
import fr.damnardev.twitch.bot.database.entity.DbRaidConfiguration;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql("/fr/damnardev/twitch/bot/database/repository/db_channel_repository_cascade_tests.sql")
class DbChannelRepositoryCascadeTests {

	@Autowired
	private DbChannelRepository dbChannelRepository;

	@Autowired
	private DbRaidConfigurationRepository dbRaidConfigurationRepository;

	@Autowired
	private EntityManager entityManager;

	@Test
	@Transactional
	void saveAndFlush_shouldInsertChannelAndRaidConfiguration() {
		// When
		var dbRaidConfiguration = DbRaidConfiguration.builder().twitchShoutoutEnabled(true).wizebotShoutoutEnabled(true)
				.raidMessageEnabled(true).messages(Collections.singletonList("message")).build();
		var dbChannel = DbChannel.builder().id(2L).name("channel_02").enabled(true).online(true).raidConfiguration(dbRaidConfiguration).build();
		dbRaidConfiguration.setChannel(dbChannel);
		var saved = this.dbChannelRepository.saveAndFlush(dbChannel);
		this.entityManager.clear();
		var loaded = this.dbChannelRepository.findById(saved.getId());

		// Then
		assertThat(loaded).isPresent().get().usingRecursiveComparison()
				.ignoringFields("commands", "raidConfiguration")
				.isEqualTo(saved);
		dbRaidConfiguration.setId(2L);
		assertThat(loaded).isPresent().get().extracting(DbChannel::getRaidConfiguration)
				.usingRecursiveComparison().ignoringFields("channel")
				.isEqualTo(dbRaidConfiguration);
	}

	@Test
	@Transactional
	void saveAndFlush_shouldUpdateChannel() {
		// When
		var dbChannel = DbChannel.builder().id(1L).name("channel_01").enabled(true).online(true).build();
		var saved = this.dbChannelRepository.saveAndFlush(dbChannel);
		this.entityManager.clear();
		var loaded = this.dbChannelRepository.findById(saved.getId());

		// Then
		var dbRaidConfiguration = DbRaidConfiguration.builder().id(1L).twitchShoutoutEnabled(true).wizebotShoutoutEnabled(true)
				.raidMessageEnabled(true).messages(Collections.singletonList("raid_message_01")).build();

		assertThat(loaded).isPresent().get().usingRecursiveComparison()
				.ignoringFields("commands", "raidConfiguration")
				.isEqualTo(saved);
		assertThat(loaded).isPresent().get().extracting(DbChannel::getRaidConfiguration)
				.usingRecursiveComparison().ignoringFields("channel")
				.isEqualTo(dbRaidConfiguration);
	}

	@Test
	@Transactional
	void deleteById_shouldDeleteChannelAndRaidConfiguration() {
		// When;
		this.dbChannelRepository.deleteById(1L);
		this.entityManager.flush();
		this.entityManager.clear();

		var loadedChannel = this.dbChannelRepository.findById(1L);
		var loadedRaidConfiguration = this.dbRaidConfigurationRepository.findById(1L);

		// Then

		assertThat(loadedChannel).isEmpty();
		assertThat(loadedRaidConfiguration).isEmpty();
	}

}
