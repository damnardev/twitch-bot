package fr.damnardev.twitch.bot.domain.service;

import java.util.Collections;

import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.model.event.ChannelFindEvent;
import fr.damnardev.twitch.bot.domain.model.event.ErrorEvent;
import fr.damnardev.twitch.bot.domain.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.domain.port.secondary.FindChannelRepository;
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
import static org.mockito.BDDMockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class DefaultFindAllChannelServiceTests {

	private DefaultFindAllChannelService findAllChannelService;

	private DefaultTryService tryService;

	@Mock
	private FindChannelRepository findChannelRepository;

	@Mock
	private EventPublisher eventPublisher;

	@BeforeEach
	void setUp() {
		this.tryService = new DefaultTryService(this.eventPublisher);
		this.tryService = spy(this.tryService);
		this.findAllChannelService = new DefaultFindAllChannelService(this.tryService, this.findChannelRepository, this.eventPublisher);
	}

	@Test
	void findAll_shouldPublishEvent_whenExceptionThrown() {
		// Given
		var name = "name";
		var exception = new RuntimeException();
		var event = ErrorEvent.builder().exception(exception).build();

		given(this.findChannelRepository.findAll()).willThrow(exception);
		doNothing().when(this.eventPublisher).publish(event);

		// When
		this.findAllChannelService.findAll();

		// Then
		then(this.tryService).should().doTry(any());
		then(this.findChannelRepository).should().findAll();
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.eventPublisher);
	}

	@Test
	void findAll_shouldPublishEvent_whenListFound() {
		// Given
		var channel = Channel.builder().build();
		var channels = Collections.singletonList(channel);
		var event = ChannelFindEvent.builder().channels(channels).build();

		given(this.findChannelRepository.findAll()).willReturn(channels);
		doNothing().when(this.eventPublisher).publish(event);

		// When
		this.findAllChannelService.findAll();

		// Then
		then(this.tryService).should().doTry(any());
		then(this.findChannelRepository).should().findAll();
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.eventPublisher);
	}

}
