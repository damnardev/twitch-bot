package fr.damnardev.twitch.bot.database.repository;

import fr.damnardev.twitch.bot.database.entity.DbChannel;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql("/fr/damnardev/twitch/bot/database/repository/db_channel_repository_tests.sql")
class DbChannelRepositoryTests {

	@Autowired
	private DbChannelRepository dbChannelRepository;

	@Test
	@Transactional(readOnly = true)
	void findAllEnabled_shouldReturnAllEnabledChannels() {
		// When
		var channels = this.dbChannelRepository.findAllEnabled();

		// Then
		var channel_01 = DbChannel.builder().id(1L).name("channel_01").enabled(true).online(true).build();
		var channel_03 = DbChannel.builder().id(3L).name("channel_03").enabled(true).build();
		assertThat(channels).isNotNull().hasSize(2)
				.usingRecursiveFieldByFieldElementComparatorIgnoringFields("dbChannelCommand", "dbChannelRaid")
				.contains(channel_01, channel_03);
	}

}
