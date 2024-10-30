package fr.damnardev.twitch.bot.primary.adapter;

import fr.damnardev.twitch.bot.domain.port.primary.IAuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class ConnectionScheduleListenerTests {

	@InjectMocks
	private ConnectionScheduleListener connectionScheduleListener;

	@Mock
	private IAuthenticationService authenticationService;

	@Test
	void schedule_shouldDoNothing() {
		// Given
		given(this.authenticationService.isInitialized()).willReturn(false);

		// When
		this.connectionScheduleListener.schedule();

		// Then
		then(this.authenticationService).should().isInitialized();
		verifyNoMoreInteractions(this.authenticationService);

	}

	@Test
	void schedule_shouldTryRenewToken() {
		// Given
		given(this.authenticationService.isInitialized()).willReturn(true);

		// When
		this.connectionScheduleListener.schedule();

		// Then
		then(this.authenticationService).should().isInitialized();
		then(this.authenticationService).should().tryRenew();
		verifyNoMoreInteractions(this.authenticationService);

	}

}
