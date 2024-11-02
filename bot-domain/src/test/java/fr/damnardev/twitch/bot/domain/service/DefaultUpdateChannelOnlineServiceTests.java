package fr.damnardev.twitch.bot.domain.service;

import java.util.Optional;

import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.model.event.ChannelUpdatedEvent;
import fr.damnardev.twitch.bot.domain.model.event.ErrorEvent;
import fr.damnardev.twitch.bot.domain.model.form.UpdateChannelOnlineForm;
import fr.damnardev.twitch.bot.domain.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.domain.port.secondary.FindChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.UpdateChannelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class DefaultUpdateChannelOnlineServiceTests {

	private DefaultUpdateChannelOnlineService updateChannelOnlineService;

	private DefaultTryService tryService;

	@Mock
	private FindChannelRepository findChannelRepository;

	@Mock
	private UpdateChannelRepository updateChannelRepository;

	@Mock
	private EventPublisher eventPublisher;

	@BeforeEach
	void setUp() {
		this.tryService = new DefaultTryService(this.eventPublisher);
		this.tryService = spy(this.tryService);
		this.updateChannelOnlineService = new DefaultUpdateChannelOnlineService(this.tryService, this.findChannelRepository, this.updateChannelRepository, this.eventPublisher);
	}

	@Test
	void process_shouldPublishEvent_whenExceptionThrown() {
		// Given
		var name = "name";
		var form = UpdateChannelOnlineForm.builder().name(name).online(true).build();
		var exception = new RuntimeException();
		var event = ErrorEvent.builder().exception(exception).build();

		given(this.findChannelRepository.findByName(name)).willThrow(exception);
		doNothing().when(this.eventPublisher).publish(event);

		// When
		this.updateChannelOnlineService.process(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(name);
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.updateChannelRepository, this.eventPublisher);
	}

	@Test
	void process_shouldPublishEvent_whenChannelNotFound() {
		// Given
		var name = "name";
		var form = UpdateChannelOnlineForm.builder().name(name).online(true).build();
		var event = ChannelUpdatedEvent.builder().error("Channel not found").build();

		given(this.findChannelRepository.findByName(name)).willReturn(Optional.empty());
		doNothing().when(this.eventPublisher).publish(event);

		// When
		this.updateChannelOnlineService.process(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(name);
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.updateChannelRepository, this.eventPublisher);
	}


	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void process_shouldUpdateChannelAndPublishEvent_whenChannelExists(boolean value) {
		// Given
		var name = "name";
		var form = UpdateChannelOnlineForm.builder().name(name).online(value).build();
		var channel = Channel.builder().id(1L).name(name).online(!value).build();
		var updatedChannel = channel.toBuilder().online(value).build();
		var event = ChannelUpdatedEvent.builder().channel(updatedChannel).build();

		given(this.findChannelRepository.findByName(name)).willReturn(Optional.of(channel));
		doNothing().when(this.updateChannelRepository).update(updatedChannel);
		doNothing().when(this.eventPublisher).publish(event);

		// When
		this.updateChannelOnlineService.process(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(name);
		then(this.updateChannelRepository).should().update(updatedChannel);
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.updateChannelRepository, this.eventPublisher);
	}

}
