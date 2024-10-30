package fr.damnardev.twitch.bot.domain.service;

import java.util.Collections;

import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.port.secondary.IAuthenticationRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.IChatRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.IFindChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.IStreamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.BDDMockito.doNothing;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class StartupServiceTests {

	@InjectMocks
	private StartupService startupService;

	@Mock
	private IAuthenticationRepository authenticationRepository;

	@Mock
	private IFindChannelRepository findChannelRepository;

	@Mock
	private IChatRepository chatRepository;

	@Mock
	private IStreamRepository streamRepository;

	@Test
	void run_shouldRenewAuthenticationAndInitializeStreams() {
		// Given
		var channel = Channel.builder().id(1L).name("name").enabled(true).online(false).build();
		var channels = Collections.singletonList(channel);

		var channelAfterCompute = channel.toBuilder().online(true).build();
		var channelsAfterCompute = Collections.singletonList(channelAfterCompute);

		given(this.authenticationRepository.renew()).willReturn(true);
		given(this.findChannelRepository.findAllEnabled()).willReturn(channels);
		doNothing().when(this.chatRepository).joinAll(channels);
		doNothing().when(this.chatRepository).reconnect();

		given(this.streamRepository.computeAll(channels)).willReturn(channelsAfterCompute);

		// When
		this.startupService.run();

		// Then
		then(this.authenticationRepository).should().renew();
		then(this.findChannelRepository).should().findAllEnabled();
		then(this.chatRepository).should().joinAll(channels);
		then(this.chatRepository).should().reconnect();
		then(this.streamRepository).should().computeAll(channels);

		verifyNoMoreInteractions(this.authenticationRepository, this.findChannelRepository, this.chatRepository, this.streamRepository);
	}

}
