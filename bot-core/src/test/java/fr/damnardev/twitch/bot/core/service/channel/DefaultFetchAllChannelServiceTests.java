package fr.damnardev.twitch.bot.core.service.channel;

import java.util.Collections;

import fr.damnardev.twitch.bot.core.service.DefaultTryService;
import fr.damnardev.twitch.bot.model.Channel;
import fr.damnardev.twitch.bot.model.event.ChannelFetchedAllEvent;
import fr.damnardev.twitch.bot.model.event.ErrorEvent;
import fr.damnardev.twitch.bot.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.port.secondary.channel.FindChannelRepository;
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
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class DefaultFetchAllChannelServiceTests {

	private DefaultFetchAllChannelService findAllChannelService;

	private DefaultTryService tryService;

	@Mock
	private FindChannelRepository findChannelRepository;

	@Mock
	private EventPublisher eventPublisher;

	@BeforeEach
	void setUp() {
		this.tryService = new DefaultTryService(this.eventPublisher);
		this.tryService = spy(this.tryService);
		this.findAllChannelService = new DefaultFetchAllChannelService(this.tryService, this.findChannelRepository, this.eventPublisher);
	}

	@Test
	void process_shouldPublishEvent_whenExceptionThrown() {
		// Given
		var exception = new RuntimeException();
		var event = ErrorEvent.builder().exception(exception).build();

		given(this.findChannelRepository.findAll()).willThrow(exception);

		// When
		this.findAllChannelService.process();

		// Then
		then(this.tryService).should().doTry(any());
		then(this.findChannelRepository).should().findAll();
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.eventPublisher);
	}

	@Test
	void process_shouldPublishEvent_whenListFound() {
		// Given
		var channel = Channel.builder().build();
		var channels = Collections.singletonList(channel);
		var event = ChannelFetchedAllEvent.builder().channels(channels).build();

		given(this.findChannelRepository.findAll()).willReturn(channels);

		// When
		this.findAllChannelService.process();

		// Then
		then(this.tryService).should().doTry(any());
		then(this.findChannelRepository).should().findAll();
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.eventPublisher);
	}

}
