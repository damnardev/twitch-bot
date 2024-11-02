package fr.damnardev.twitch.bot.domain.service;

import java.util.Optional;

import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.model.event.ChannelCreatedEvent;
import fr.damnardev.twitch.bot.domain.model.event.ErrorEvent;
import fr.damnardev.twitch.bot.domain.model.form.CreateChannelForm;
import fr.damnardev.twitch.bot.domain.port.secondary.CreateChannelRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class DefaultCreateChannelServiceTests {

	private DefaultCreateChannelService createChannelService;

	private DefaultTryService tryService;

	@Mock
	private FindChannelRepository findChannelRepository;

	@Mock
	private CreateChannelRepository createChannelRepository;

	@Mock
	private EventPublisher eventPublisher;

	@BeforeEach
	void setUp() {
		this.tryService = new DefaultTryService(this.eventPublisher);
		this.tryService = spy(this.tryService);
		this.createChannelService = new DefaultCreateChannelService(this.tryService, this.findChannelRepository, this.createChannelRepository, this.eventPublisher);
	}

	@Test
	void save_shouldPublishEvent_whenExceptionThrown() {
		// Given
		var name = "name";
		var form = CreateChannelForm.builder().name(name).build();
		var exception = new RuntimeException();
		var event = ErrorEvent.builder().exception(exception).build();

		given(this.findChannelRepository.findByName(name)).willThrow(exception);
		doNothing().when(this.eventPublisher).publish(event);

		// When
		this.createChannelService.save(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(name);
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.createChannelRepository, this.eventPublisher);
	}

	@Test
	void save_shouldPublishEvent_whenChannelAlreadyExists() {
		// Given
		var name = "name";
		var form = CreateChannelForm.builder().name(name).build();
		var event = ChannelCreatedEvent.builder().error("Channel already exists").build();

		given(this.findChannelRepository.findByName(name)).willReturn(Optional.of(Channel.builder().build()));
		doNothing().when(this.eventPublisher).publish(event);

		// When
		this.createChannelService.save(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(name);
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.createChannelRepository, this.eventPublisher);
	}

	@Test
	void save_shouldCreateAndPublishEvent_whenChannelDoesNotExist() {
		// Given
		var name = "name";
		var form = CreateChannelForm.builder().name(name).build();
		var channel = Channel.builder().name(name).build();
		var event = ChannelCreatedEvent.builder().channel(channel).build();

		given(this.findChannelRepository.findByName(name)).willReturn(Optional.empty());
		given(this.createChannelRepository.save(channel)).willReturn(channel);

		// When
		this.createChannelService.save(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(name);
		then(this.createChannelRepository).should().save(channel);
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.createChannelRepository, this.eventPublisher);
	}

}
