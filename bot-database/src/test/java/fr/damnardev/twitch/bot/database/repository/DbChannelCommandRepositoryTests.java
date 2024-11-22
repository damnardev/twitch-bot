package fr.damnardev.twitch.bot.database.repository;

import java.util.List;

import fr.damnardev.twitch.bot.database.entity.DbChannelCommand;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql("/fr/damnardev/twitch/bot/database/repository/db_channel_command_repository_tests.sql")
class DbChannelCommandRepositoryTests {

	@Autowired
	private DbChannelCommandRepository dbChannelCommandRepository;

	@Test
	@Transactional(readOnly = true)
	void findByChannelName_shouldReturnEmptyList_whenChannelNameNotFound() {
		// When
		var result = this.dbChannelCommandRepository.findByChannelName("name");

		// Then
		assertThat(result).isEmpty();
	}

	@Test
	@Transactional(readOnly = true)
	void findByChannelName_shouldReturnEmptyList_whenChannelNameExistButNoCommand() {
		// When
		var result = this.dbChannelCommandRepository.findByChannelName("channel_02");

		// Then
		assertThat(result).isEmpty();
	}

	@Test
	@Transactional(readOnly = true)
	void findByChannelName_shouldReturnListChannelConfiguration_whenNameFound() {
		// When
		var result = this.dbChannelCommandRepository.findByChannelName("channel_01");

		// Then
		var expected = DbChannelCommand.builder().id(1L).name("!foo")
				.enabled(true)
				.cooldown(60)
				.messages(List.of("channel_command_message_01")).build();
		assertThat(result).hasSize(1).first()
				.usingRecursiveComparison().ignoringFields("channel")
				.isEqualTo(expected);
	}

	@Test
	@Transactional(readOnly = true)
	void findByChannelNameAndName_shouldReturnOptionalEmpty_whenChannelNameNotFound() {
		// When
		var result = this.dbChannelCommandRepository.findByChannelNameAndName("channel_02", "!foo");

		// Then
		assertThat(result).isEmpty();
	}

	@Test
	@Transactional(readOnly = true)
	void findByChannelNameAndName_shouldReturnOptionalEmpty_whenChannelNameFoundButNotName() {
		// When
		var result = this.dbChannelCommandRepository.findByChannelNameAndName("channel_01", "!bar");

		// Then
		assertThat(result).isEmpty();
	}


	@Test
	@Transactional(readOnly = true)
	void findByChannelNameAndName_shouldReturnOptionalChannelConfiguration_whenChannelNameAndNameFound() {
		// When
		var result = this.dbChannelCommandRepository.findByChannelNameAndName("channel_01", "!foo");

		// Then
		var expected = DbChannelCommand.builder().id(1L).name("!foo")
				.enabled(true)
				.cooldown(60)
				.messages(List.of("channel_command_message_01")).build();
		assertThat(result).isNotEmpty().get()
				.usingRecursiveComparison().ignoringFields("channel")
				.isEqualTo(expected);
	}

}
