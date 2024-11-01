package fr.damnardev.twitch.bot.domain.service;

import fr.damnardev.twitch.bot.domain.port.secondary.AuthenticationRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.ChatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class DefaultAuthenticationServiceTests {

	@InjectMocks
	private DefaultAuthenticationService startupService;

	@Mock
	private AuthenticationRepository authenticationRepository;

	@Mock
	private ChatRepository chatRepository;

	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void isInitialized_shouldReturnInitializedValue(boolean value) {
		// Given
		given(this.authenticationRepository.isInitialized()).willReturn(value);

		// When
		var result = this.startupService.isInitialized();

		// Then
		then(this.authenticationRepository).should().isInitialized();
		verifyNoMoreInteractions(this.authenticationRepository, this.chatRepository);

		assertThat(result).isEqualTo(value);
	}

	@Test
	void tryRenew_shouldDoNothing_whenTokenIsValid() {
		// Given
		given(this.authenticationRepository.isValid()).willReturn(true);

		// When
		this.startupService.tryRenew();

		// Then
		then(this.authenticationRepository).should().isValid();
		verifyNoMoreInteractions(this.authenticationRepository, this.chatRepository);
	}

	@Test
	void tryRenew_shouldRenewAndReconnect_whenTokenIsNotValid() {
		// Given
		given(this.authenticationRepository.isValid()).willReturn(false);
		given(this.authenticationRepository.renew()).willReturn(true);

		// When
		this.startupService.tryRenew();

		// Then
		then(this.authenticationRepository).should().isValid();
		then(this.authenticationRepository).should().renew();
		then(this.chatRepository).should().reconnect();
		verifyNoMoreInteractions(this.authenticationRepository, this.chatRepository);
	}

}
