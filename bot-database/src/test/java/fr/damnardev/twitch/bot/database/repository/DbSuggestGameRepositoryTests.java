package fr.damnardev.twitch.bot.database.repository;

import fr.damnardev.twitch.bot.database.entity.DbSuggestGame;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql("/fr/damnardev/twitch/bot/database/repository/db_suggest_game_repository_tests.sql")
class DbSuggestGameRepositoryTests {

	@Autowired
	private DbSuggestGameRepository dbSuggestGameRepository;

	@Test
	@Transactional(readOnly = true)
	void findByChannelName_shouldReturnOptionalEmpty_whenNameNotFound() {
		// When
		var result = this.dbSuggestGameRepository.findByChannelName("name");

		// Then
		assertThat(result).isEmpty();
	}

	@Test
	@Transactional(readOnly = true)
	void findByChannelName_shouldReturnOptionalEmpty_whenNameExistButNoRaidConfiguration() {
		// When
		var result = this.dbSuggestGameRepository.findByChannelName("channel_02");

		// Then
		assertThat(result).isEmpty();
	}

	@Test
	@Transactional(readOnly = true)
	void findByChannelName_shouldReturnOptionalRaidConfiguration_whenNameFound() {
		// When
		var result = this.dbSuggestGameRepository.findByChannelName("channel_01");

		// Then
		var expected = DbSuggestGame.builder().id(1L).formId("111").viewerFieldId("222").gameFieldId("333").build();
		assertThat(result).isPresent().get()
				.usingRecursiveComparison().ignoringFields("channel")
				.isEqualTo(expected);
	}

}
