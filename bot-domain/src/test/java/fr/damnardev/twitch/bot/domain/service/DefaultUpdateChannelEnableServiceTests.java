package fr.damnardev.twitch.bot.domain.service;

import java.util.Optional;

import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.model.event.ChannelUpdatedEvent;
import fr.damnardev.twitch.bot.domain.model.event.ErrorEvent;
import fr.damnardev.twitch.bot.domain.model.form.UpdateChannelEnabledForm;
import fr.damnardev.twitch.bot.domain.port.secondary.ChatRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.domain.port.secondary.FindChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.StreamRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.UpdateChannelRepository;
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
class DefaultUpdateChannelEnableServiceTests {

	private DefaultUpdateChannelEnableService updateEnableChannelService;

	private DefaultTryService tryService;

	@Mock
	private FindChannelRepository findChannelRepository;

	@Mock
	private UpdateChannelRepository updateChannelRepository;

	@Mock
	private ChatRepository chatRepository;

	@Mock
	private StreamRepository streamRepository;

	@Mock
	private EventPublisher eventPublisher;

	@BeforeEach
	void setUp() {
		this.tryService = new DefaultTryService(this.eventPublisher);
		this.tryService = spy(this.tryService);
		this.updateEnableChannelService = new DefaultUpdateChannelEnableService(this.tryService, this.findChannelRepository, this.updateChannelRepository, this.chatRepository, this.streamRepository, this.eventPublisher);
	}

	@Test
	void updateEnabled_shouldPublishEvent_whenExceptionThrown() {
		// Given
		var name = "name";
		var form = UpdateChannelEnabledForm.builder().name(name).enabled(true).build();
		var exception = new RuntimeException();
		var event = ErrorEvent.builder().exception(exception).build();

		given(this.findChannelRepository.findByName(name)).willThrow(exception);
		doNothing().when(this.eventPublisher).publish(event);

		// When
		this.updateEnableChannelService.updateEnabled(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(name);
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.updateChannelRepository, this.chatRepository, this.streamRepository, this.eventPublisher);
	}

	@Test
	void updateEnabled_shouldPublishEvent_whenChannelNotFound() {
		// Given
		var name = "name";
		var form = UpdateChannelEnabledForm.builder().name(name).build();
		var event = ChannelUpdatedEvent.builder().error("Channel not found").build();

		given(this.findChannelRepository.findByName(name)).willReturn(Optional.empty());
		doNothing().when(this.eventPublisher).publish(event);

		// When
		this.updateEnableChannelService.updateEnabled(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(name);
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.updateChannelRepository, this.chatRepository, this.streamRepository, this.eventPublisher);
	}

	@Test
	void updateEnabled_shouldUpdateChannelAndPublishEvent_whenChannelExists() {
		// Given
		var name = "name";
		var form = UpdateChannelEnabledForm.builder().name(name).enabled(true).build();
		var channel = Channel.builder().id(1L).name(name).enabled(false).online(false).build();
		var updatedChannel_01 = channel.toBuilder().enabled(true).online(false).build();
		var updatedChannel_02 = channel.toBuilder().enabled(true).online(true).build();
		var event = ChannelUpdatedEvent.builder().channel(updatedChannel_02).build();

		given(this.findChannelRepository.findByName(name)).willReturn(Optional.of(channel));
		doNothing().when(this.updateChannelRepository).update(updatedChannel_01);
		given(this.streamRepository.compute(updatedChannel_01)).willReturn(updatedChannel_02);
		doNothing().when(this.chatRepository).join(updatedChannel_02);
		doNothing().when(this.eventPublisher).publish(event);

		// When
		this.updateEnableChannelService.updateEnabled(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(name);
		then(this.updateChannelRepository).should().update(updatedChannel_01);
		then(this.streamRepository).should().compute(updatedChannel_01);
		then(this.chatRepository).should().join(updatedChannel_02);
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.updateChannelRepository, this.chatRepository, this.streamRepository, this.eventPublisher);
	}

	@Test
	void updateEnabled_shouldUpdateChannelAndLeaveAndPublishEvent_whenChannelExists() {
		// Given
		var name = "name";
		var form = UpdateChannelEnabledForm.builder().name(name).enabled(false).build();
		var channel = Channel.builder().id(1L).name(name).enabled(true).online(true).build();
		var updatedChannel = channel.toBuilder().enabled(false).online(false).build();
		var event = ChannelUpdatedEvent.builder().channel(updatedChannel).build();

		given(this.findChannelRepository.findByName(name)).willReturn(Optional.of(channel));
		doNothing().when(this.updateChannelRepository).update(updatedChannel);
		doNothing().when(this.chatRepository).leave(updatedChannel);
		doNothing().when(this.eventPublisher).publish(event);

		// When
		this.updateEnableChannelService.updateEnabled(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(name);
		then(this.chatRepository).should().leave(updatedChannel);
		then(this.updateChannelRepository).should().update(updatedChannel);
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.updateChannelRepository, this.chatRepository, this.streamRepository, this.eventPublisher);
	}

}
