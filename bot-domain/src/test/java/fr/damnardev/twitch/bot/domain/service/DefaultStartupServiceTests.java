package fr.damnardev.twitch.bot.domain.service;

import java.util.Collections;

import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.port.secondary.AuthenticationRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.ChatRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.domain.port.secondary.StreamRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.channel.FindChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.channel.UpdateChannelRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class DefaultStartupServiceTests {

	@InjectMocks
	private DefaultStartupService startupService;

	@Mock
	private AuthenticationRepository authenticationRepository;

	@Mock
	private FindChannelRepository findChannelRepository;

	@Mock
	private ChatRepository chatRepository;

	@Mock
	private StreamRepository streamRepository;

	@Mock
	private UpdateChannelRepository updateChannelRepository;

	@Mock
	private EventPublisher eventPublisher;

	@Test
	void run_shouldRenewAuthenticationAndInitializeStreams() {
		// Given
		var channel = Channel.builder().id(1L).name("channelName").enabled(true).online(false).build();
		var channels = Collections.singletonList(channel);

		var channelAfterCompute = channel.toBuilder().online(true).build();
		var channelsAfterCompute = Collections.singletonList(channelAfterCompute);

		given(this.authenticationRepository.renew()).willReturn(true);
		given(this.findChannelRepository.findAllEnabled()).willReturn(channels);
		given(this.streamRepository.computeOnline(channels)).willReturn(channelsAfterCompute);

		// When
		this.startupService.run();

		// Then
		then(this.authenticationRepository).should().renew();
		then(this.findChannelRepository).should().findAllEnabled();
		then(this.chatRepository).should().joinAll(channels);
		then(this.chatRepository).should().reconnect();
		then(this.streamRepository).should().computeOnline(channels);
		then(this.eventPublisher).should().publish(any());
		verifyNoMoreInteractions(this.authenticationRepository, this.findChannelRepository, this.chatRepository, this.streamRepository, this.eventPublisher);
	}

}
