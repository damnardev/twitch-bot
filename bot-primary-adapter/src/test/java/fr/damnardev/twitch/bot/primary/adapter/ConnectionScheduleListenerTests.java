package fr.damnardev.twitch.bot.primary.adapter;

import fr.damnardev.twitch.bot.domain.port.primary.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class ConnectionScheduleListenerTests {

	@InjectMocks
	private ConnectionScheduleListener connectionScheduleListener;

	@Mock
	private ThreadPoolTaskExecutor executor;

	@Mock
	private AuthenticationService authenticationService;

	@Test
	void schedule_shouldDoNothing_whenNotInitialized() {
		// Given
		var captor = ArgumentCaptor.forClass(Runnable.class);

		doNothing().when(this.executor).execute(captor.capture());
		given(this.authenticationService.isInitialized()).willReturn(false);

		// When
		this.connectionScheduleListener.schedule();

		// Then
		then(this.executor).should().execute(captor.capture());
		captor.getValue().run();
		then(this.authenticationService).should().isInitialized();
		verifyNoMoreInteractions(this.executor, this.authenticationService);
	}

	@Test
	void schedule_shouldTryRenewToken_whenInitialized() {
		// Given
		var captor = ArgumentCaptor.forClass(Runnable.class);

		doNothing().when(this.executor).execute(captor.capture());
		given(this.authenticationService.isInitialized()).willReturn(true);

		// When
		this.connectionScheduleListener.schedule();

		// Then
		then(this.executor).should().execute(captor.capture());
		captor.getValue().run();
		then(this.authenticationService).should().isInitialized();
		then(this.authenticationService).should().tryRenew();
		verifyNoMoreInteractions(this.executor, this.authenticationService);
	}

}
