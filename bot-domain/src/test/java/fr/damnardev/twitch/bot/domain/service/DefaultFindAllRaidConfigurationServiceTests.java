package fr.damnardev.twitch.bot.domain.service;

import java.util.Collections;

import fr.damnardev.twitch.bot.domain.model.RaidConfiguration;
import fr.damnardev.twitch.bot.domain.model.event.ErrorEvent;
import fr.damnardev.twitch.bot.domain.model.event.RaidConfigurationFindAllEvent;
import fr.damnardev.twitch.bot.domain.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.domain.port.secondary.FindRaidConfigurationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class DefaultFindAllRaidConfigurationServiceTests {

	private DefaultFindAllRaidConfigurationService findAllRaidConfigurationService;

	private DefaultTryService tryService;

	@Mock
	private FindRaidConfigurationRepository findRaidConfigurationRepository;

	@Mock
	private EventPublisher eventPublisher;

	@BeforeEach
	void setUp() {
		this.tryService = new DefaultTryService(this.eventPublisher);
		this.tryService = spy(this.tryService);
		this.findAllRaidConfigurationService = new DefaultFindAllRaidConfigurationService(this.tryService, this.findRaidConfigurationRepository, this.eventPublisher);
	}

	@Test
	void findAll_shouldPublishEvent_whenExceptionThrown() {
		// Given
		var exception = new RuntimeException();
		var event = ErrorEvent.builder().exception(exception).build();

		given(this.findRaidConfigurationRepository.findAll()).willThrow(exception);
		doNothing().when(this.eventPublisher).publish(event);

		// When
		this.findAllRaidConfigurationService.findAll();

		// Then
		then(this.tryService).should().doTry(any());
		then(this.findRaidConfigurationRepository).should().findAll();
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findRaidConfigurationRepository, this.eventPublisher);
	}

	@Test
	void findAll_shouldPublishEvent_whenListFound() {
		// Given
		var configuration = RaidConfiguration.builder().build();
		var configurations = Collections.singletonList(configuration);
		var event = RaidConfigurationFindAllEvent.builder().configurations(configurations).build();

		given(this.findRaidConfigurationRepository.findAll()).willReturn(configurations);
		doNothing().when(this.eventPublisher).publish(event);

		// When
		this.findAllRaidConfigurationService.findAll();

		// Then
		then(this.tryService).should().doTry(any());
		then(this.findRaidConfigurationRepository).should().findAll();
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findRaidConfigurationRepository, this.eventPublisher);
	}

}
