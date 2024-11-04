package fr.damnardev.twitch.bot.secondary.adapter;

import java.util.Collections;
import java.util.Optional;

import fr.damnardev.twitch.bot.database.entity.DbRaidConfiguration;
import fr.damnardev.twitch.bot.database.repository.DbRaidConfigurationRepository;
import fr.damnardev.twitch.bot.domain.model.RaidConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class DefaultFindRaidConfigurationRepositoryTests {

	@InjectMocks
	private DefaultFindRaidConfigurationRepository findRaidConfigurationRepository;

	@Mock
	private DbRaidConfigurationRepository dbRaidConfigurationRepository;

	@Test
	void findByChannelName_shouldReturnOptionalEmpty_whenNameNotFound() {
		// Given
		var name = "name";

		given(this.dbRaidConfigurationRepository.findByChannelName(name)).willReturn(Optional.empty());

		// When
		var result = this.findRaidConfigurationRepository.findByChannelName(name);

		// Then
		then(this.dbRaidConfigurationRepository).should().findByChannelName(name);
		verifyNoMoreInteractions(this.dbRaidConfigurationRepository);

		assertThat(result).isEmpty();
	}

	@Test
	void findByName_shouldReturnChannel_whenNameFound() {
		// Given
		var name = "name";
		var message = "message";
		var dbRaidConfiguration = DbRaidConfiguration.builder().id(1L)
				.raidMessageEnabled(false).twitchShoutoutEnabled(true).wizebotShoutoutEnabled(true)
				.messages(Collections.singletonList(message)).build();

		given(this.dbRaidConfigurationRepository.findByChannelName(name)).willReturn(Optional.of(dbRaidConfiguration));

		// When
		var result = this.findRaidConfigurationRepository.findByChannelName(name);

		// Then
		then(this.dbRaidConfigurationRepository).should().findByChannelName(name);
		verifyNoMoreInteractions(this.dbRaidConfigurationRepository);

		var expected = RaidConfiguration.builder().id(1L)
				.raidMessageEnabled(false).twitchShoutoutEnabled(true).wizebotShoutoutEnabled(true)
				.messages(Collections.singletonList(message)).build();
		assertThat(result).isPresent().get().isEqualTo(expected);
	}

}
