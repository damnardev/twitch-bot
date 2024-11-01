package fr.damnardev.twitch.bot.database.repository;

import fr.damnardev.twitch.bot.database.entity.DbCredential;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql("/fr/damnardev/twitch/bot/database/repository/db_credential_repository_tests.sql")
class DbCredentialRepositoryTests {

	@Autowired
	private DbCredentialRepository dbCredentialRepository;

	@Test
	@Transactional(readOnly = true)
	void findLast_shouldReturnOptionalCredential_whenIdIsMaxId() {
		// When
		var optionalDbCredential = this.dbCredentialRepository.findLast();

		// Then
		var refreshToken = DbCredential.builder().id(3L).refreshToken("refresh_token_03").build();
		assertThat(optionalDbCredential).isPresent().hasValue(refreshToken);
	}

}
