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
	void findAllEnabled_shouldReturnListOfChannel_whenEnabledIsTrue() {
		// When
		var result = this.dbChannelRepository.findAllEnabled();

		// Then
		var expected_01 = DbChannel.builder().id(1L).name("channel_01").enabled(true).online(true).build();
		var expected_03 = DbChannel.builder().id(3L).name("channel_03").enabled(true).build();
		assertThat(result).isNotNull().hasSize(2)
				.usingRecursiveFieldByFieldElementComparatorIgnoringFields("dbChannelCommand", "dbChannelRaid")
				.contains(expected_01, expected_03);
	}

	@Test
	@Transactional(readOnly = true)
	void findByName_shouldReturnOptionalChannel_whenNameAreSame() {
		// When
		var result = this.dbChannelRepository.findByName("channel_01");

		// Then
		var expected = DbChannel.builder().id(1L).name("channel_01").enabled(true).online(true).build();
		assertThat(result).isPresent().get().usingRecursiveComparison().ignoringFields("dbChannelCommand", "dbChannelRaid")
				.isEqualTo(expected);
	}

	@Test
	@Transactional(readOnly = true)
	void findByName_shouldReturnOptionalEmpty_whenNameNotFound() {
		// When
		var result = this.dbChannelRepository.findByName("channel_05");

		// Then
		assertThat(result).isEmpty();
	}

}
