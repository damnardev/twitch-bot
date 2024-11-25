package fr.damnardev.twitch.bot.core.service.channel;

import java.util.Optional;

import fr.damnardev.twitch.bot.core.service.DefaultTryService;
import fr.damnardev.twitch.bot.exception.BusinessException;
import fr.damnardev.twitch.bot.model.Channel;
import fr.damnardev.twitch.bot.model.event.ChannelUpdatedEvent;
import fr.damnardev.twitch.bot.model.event.ErrorEvent;
import fr.damnardev.twitch.bot.model.form.UpdateChannelForm;
import fr.damnardev.twitch.bot.port.secondary.ChatRepository;
import fr.damnardev.twitch.bot.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.port.secondary.StreamRepository;
import fr.damnardev.twitch.bot.port.secondary.channel.FindChannelRepository;
import fr.damnardev.twitch.bot.port.secondary.channel.UpdateChannelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class DefaultUpdateChannelServiceTests {

	private DefaultUpdateChannelService updateEnableChannelService;

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
		this.updateEnableChannelService = new DefaultUpdateChannelService(this.tryService, this.findChannelRepository, this.updateChannelRepository, this.chatRepository, this.streamRepository, this.eventPublisher);
	}

	@Test
	void process_shouldPublishEvent_whenChannelNotFound() {
		// Given
		var channelName = "channelName";
		var form = UpdateChannelForm.builder().name(channelName).build();
		var captor = ArgumentCaptor.forClass(ErrorEvent.class);

		given(this.findChannelRepository.findByName(channelName)).willReturn(Optional.empty());

		// When
		this.updateEnableChannelService.process(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(channelName);
		then(this.eventPublisher).should().publish(captor.capture());
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.updateChannelRepository, this.chatRepository, this.streamRepository, this.eventPublisher);

		var event = captor.getValue();
		assertThat(event.getException()).isNotNull()
				.isInstanceOf(BusinessException.class)
				.hasMessage("Channel not found");
	}

	@Test
	void process_shouldUpdateChannelAndPublishEvent_whenChannelExists() {
		// Given
		var channelName = "channelName";
		var form = UpdateChannelForm.builder().name(channelName).enabled(true).build();
		var channel = Channel.builder().id(1L).name(channelName).enabled(false).online(false).build();
		var updatedChannel_01 = channel.toBuilder().enabled(true).online(false).build();
		var updatedChannel_02 = channel.toBuilder().enabled(true).online(true).build();
		var event = ChannelUpdatedEvent.builder().channel(updatedChannel_02).build();

		given(this.findChannelRepository.findByName(channelName)).willReturn(Optional.of(channel));
		given(this.streamRepository.computeOnline(updatedChannel_01)).willReturn(updatedChannel_02);

		// When
		this.updateEnableChannelService.process(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(channelName);
		then(this.streamRepository).should().computeOnline(updatedChannel_01);
		then(this.chatRepository).should().join(updatedChannel_02);
		then(this.updateChannelRepository).should().update(updatedChannel_02);
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.updateChannelRepository, this.chatRepository, this.streamRepository, this.eventPublisher);
	}

	@Test
	void process_shouldUpdateChannelAndLeaveAndPublishEvent_whenChannelExists() {
		// Given
		var channelName = "channelName";
		var form = UpdateChannelForm.builder().name(channelName).enabled(false).build();
		var channel = Channel.builder().id(1L).name(channelName).enabled(true).online(true).build();
		var updatedChannel = channel.toBuilder().enabled(false).online(false).build();
		var event = ChannelUpdatedEvent.builder().channel(updatedChannel).build();

		given(this.findChannelRepository.findByName(channelName)).willReturn(Optional.of(channel));

		// When
		this.updateEnableChannelService.process(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(channelName);
		then(this.chatRepository).should().leave(updatedChannel);
		then(this.updateChannelRepository).should().update(updatedChannel);
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.updateChannelRepository, this.chatRepository, this.streamRepository, this.eventPublisher);
	}

	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void process_shouldUpdateChannelAndPublishEvent_whenChannelExists(boolean value) {
		// Given
		var channelName = "channelName";
		var form = UpdateChannelForm.builder().name(channelName).online(value).build();
		var channel = Channel.builder().id(1L).name(channelName).online(!value).build();
		var updatedChannel = channel.toBuilder().online(value).build();
		var event = ChannelUpdatedEvent.builder().channel(updatedChannel).build();

		given(this.findChannelRepository.findByName(channelName)).willReturn(Optional.of(channel));

		// When
		this.updateEnableChannelService.process(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(channelName);
		then(this.updateChannelRepository).should().update(updatedChannel);
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.updateChannelRepository, this.eventPublisher);
	}

}
