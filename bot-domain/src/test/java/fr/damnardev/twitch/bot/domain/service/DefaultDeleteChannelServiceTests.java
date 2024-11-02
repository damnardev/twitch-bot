package fr.damnardev.twitch.bot.domain.service;

import java.util.Optional;

import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.model.event.ChannelDeletedEvent;
import fr.damnardev.twitch.bot.domain.model.event.ErrorEvent;
import fr.damnardev.twitch.bot.domain.model.form.DeleteChannelForm;
import fr.damnardev.twitch.bot.domain.port.secondary.ChatRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.DeleteChannelRepository;
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
class DefaultDeleteChannelServiceTests {

	private DefaultDeleteChannelService deleteChannelService;

	private DefaultTryService tryService;

	@Mock
	private FindChannelRepository findChannelRepository;

	@Mock
	private DeleteChannelRepository deleteChannelRepository;

	@Mock
	private ChatRepository chatRepository;

	@Mock
	private EventPublisher eventPublisher;

	@BeforeEach
	void setUp() {
		this.tryService = new DefaultTryService(this.eventPublisher);
		this.tryService = spy(this.tryService);
		this.deleteChannelService = new DefaultDeleteChannelService(this.tryService, this.findChannelRepository, this.deleteChannelRepository, this.chatRepository, this.eventPublisher);
	}

	@Test
	void delete_shouldPublishEvent_whenExceptionThrown() {
		// Given
		var name = "name";
		var form = DeleteChannelForm.builder().name(name).build();
		var exception = new RuntimeException();
		var event = ErrorEvent.builder().exception(exception).build();

		given(this.findChannelRepository.findByName(name)).willThrow(exception);
		doNothing().when(this.eventPublisher).publish(event);

		// When
		this.deleteChannelService.delete(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(name);
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.deleteChannelRepository, this.chatRepository, this.eventPublisher);
	}

	@Test
	void delete_shouldPublishEvent_whenChannelNotFound() {
		// Given
		var name = "name";
		var form = DeleteChannelForm.builder().name(name).build();
		var event = ChannelDeletedEvent.builder().error("Channel not found").build();

		given(this.findChannelRepository.findByName(name)).willReturn(Optional.empty());
		doNothing().when(this.eventPublisher).publish(event);

		// When
		this.deleteChannelService.delete(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(name);
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.deleteChannelRepository, this.chatRepository, this.eventPublisher);
	}

	@Test
	void delete_shouldDeleteChannelAndPublishEvent_whenChannelFound() {
		// Given
		var name = "name";
		var form = DeleteChannelForm.builder().name(name).build();
		var channel = Channel.builder().id(1L).name(name).build();
		var event = ChannelDeletedEvent.builder().channel(channel).build();

		given(this.findChannelRepository.findByName(name)).willReturn(Optional.of(channel));
		doNothing().when(this.deleteChannelRepository).delete(channel);
		doNothing().when(this.eventPublisher).publish(event);

		// When
		this.deleteChannelService.delete(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(name);
		then(this.deleteChannelRepository).should().delete(channel);
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.deleteChannelRepository, this.chatRepository, this.eventPublisher);
	}

	@Test
	void delete_shouldDeleteChannelLeaveAndPublishEvent_whenChannelFoundAndEnabled() {
		// Given
		var name = "name";
		var form = DeleteChannelForm.builder().name(name).build();
		var channel = Channel.builder().id(1L).name(name).enabled(true).build();
		var event = ChannelDeletedEvent.builder().channel(channel).build();

		given(this.findChannelRepository.findByName(name)).willReturn(Optional.of(channel));
		doNothing().when(this.deleteChannelRepository).delete(channel);
		doNothing().when(this.chatRepository).leave(channel);
		doNothing().when(this.eventPublisher).publish(event);

		// When
		this.deleteChannelService.delete(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(name);
		then(this.deleteChannelRepository).should().delete(channel);
		then(this.chatRepository).should().leave(channel);
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.deleteChannelRepository, this.chatRepository, this.eventPublisher);
	}

}
