package fr.damnardev.twitch.bot.domain.service.channel;

import java.util.Optional;

import fr.damnardev.twitch.bot.domain.exception.BusinessException;
import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.model.event.ChannelDeletedEvent;
import fr.damnardev.twitch.bot.domain.model.event.ErrorEvent;
import fr.damnardev.twitch.bot.domain.model.form.DeleteChannelForm;
import fr.damnardev.twitch.bot.domain.port.secondary.ChatRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.domain.port.secondary.channel.DeleteChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.channel.FindChannelRepository;
import fr.damnardev.twitch.bot.domain.service.DefaultTryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
	void process_shouldPublishEvent_whenChannelNotFound() {
		// Given
		var channelName = "channelName";
		var form = DeleteChannelForm.builder().name(channelName).build();
		var captor = ArgumentCaptor.forClass(ErrorEvent.class);

		given(this.findChannelRepository.findByName(channelName)).willReturn(Optional.empty());

		// When
		this.deleteChannelService.process(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(channelName);
		then(this.eventPublisher).should().publish(captor.capture());
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.deleteChannelRepository, this.chatRepository, this.eventPublisher);

		var event = captor.getValue();
		assertThat(event.getException()).isNotNull()
				.isInstanceOf(BusinessException.class)
				.hasMessage("Channel not found");
	}

	@Test
	void process_shouldDeleteChannelAndPublishEvent_whenChannelFound() {
		// Given
		var channelName = "channelName";
		var form = DeleteChannelForm.builder().name(channelName).build();
		var channel = Channel.builder().id(1L).name(channelName).build();
		var event = ChannelDeletedEvent.builder().channel(channel).build();

		given(this.findChannelRepository.findByName(channelName)).willReturn(Optional.of(channel));

		// When
		this.deleteChannelService.process(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(channelName);
		then(this.deleteChannelRepository).should().delete(channel);
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.deleteChannelRepository, this.chatRepository, this.eventPublisher);
	}

	@Test
	void delete_shouldDeleteChannelLeaveAndPublishEvent_whenChannelFoundAndEnabled() {
		// Given
		var channelName = "channelName";
		var form = DeleteChannelForm.builder().name(channelName).build();
		var channel = Channel.builder().id(1L).name(channelName).enabled(true).build();
		var event = ChannelDeletedEvent.builder().channel(channel).build();

		given(this.findChannelRepository.findByName(channelName)).willReturn(Optional.of(channel));

		// When
		this.deleteChannelService.process(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(channelName);
		then(this.deleteChannelRepository).should().delete(channel);
		then(this.chatRepository).should().leave(channel);
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.deleteChannelRepository, this.chatRepository, this.eventPublisher);
	}

}
