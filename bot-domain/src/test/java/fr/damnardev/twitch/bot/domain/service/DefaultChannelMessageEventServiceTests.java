package fr.damnardev.twitch.bot.domain.service;

import java.util.Optional;

import fr.damnardev.twitch.bot.domain.exception.BusinessException;
import fr.damnardev.twitch.bot.domain.model.event.ErrorEvent;
import fr.damnardev.twitch.bot.domain.model.form.ChannelMessageEventForm;
import fr.damnardev.twitch.bot.domain.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.domain.port.secondary.channel.FindChannelRepository;
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
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class DefaultChannelMessageEventServiceTests {

	private DefaultChannelMessageEventService channelMessageEventService;

	private DefaultTryService tryService;

	@Mock
	private FindChannelRepository findChannelRepository;

	@Mock
	private EventPublisher eventPublisher;

	@BeforeEach
	void setUp() {
		this.tryService = new DefaultTryService(this.eventPublisher);
		this.tryService = spy(this.tryService);
		this.channelMessageEventService = new DefaultChannelMessageEventService(this.tryService, this.findChannelRepository, this.eventPublisher);
	}

	@Test
	void process_shouldPublishEvent_whenChannelNotFound() {
		// Given
		var channelName = "channelName";
		var form = ChannelMessageEventForm.builder().channelName(channelName).build();
		var captor = ArgumentCaptor.forClass(ErrorEvent.class);

		given(this.findChannelRepository.findByName(channelName)).willReturn(Optional.empty());

		// When
		this.channelMessageEventService.process(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(channelName);
		then(this.eventPublisher).should().publish(captor.capture());
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.eventPublisher);

		var event = captor.getValue();
		assertThat(event.getException()).isNotNull()
				.isInstanceOf(BusinessException.class)
				.hasMessage("Channel not found");
	}

}
