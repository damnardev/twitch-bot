package fr.damnardev.twitch.bot.primary.adapter;

import fr.damnardev.twitch.bot.domain.port.primary.StartupService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.BDDMockito.doNothing;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class ApplicationStartupListenerTests {

	@InjectMocks
	private ApplicationStartupListener applicationStartupListener;

	@Mock
	private StartupService startupService;

	@Test
	void run_shouldInvokeStartupServiceRun() {
		// Given
		doNothing().when(this.startupService).run();

		// When
		this.applicationStartupListener.run(null);

		// Then
		then(this.startupService).should().run();

		verifyNoMoreInteractions(this.startupService);
	}

}
