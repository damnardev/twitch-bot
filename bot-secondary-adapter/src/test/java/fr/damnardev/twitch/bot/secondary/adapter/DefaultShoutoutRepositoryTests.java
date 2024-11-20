package fr.damnardev.twitch.bot.secondary.adapter;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.helix.TwitchHelix;
import com.netflix.hystrix.HystrixCommand;
import fr.damnardev.twitch.bot.domain.model.Shoutout;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class DefaultShoutoutRepositoryTests {

	@InjectMocks
	private DefaultShoutoutRepository shoutoutRepository;

	@Mock
	private TwitchHelix twitchHelix;

	@Mock
	private OAuth2Credential credential;

	@Mock
	private ThreadPoolTaskExecutor executor;

	@Test
	void sendMessage_shouldSendMessage_whenCalled() {
		// Given
		var captor = ArgumentCaptor.forClass(Runnable.class);
		var shoutout = Shoutout.builder().channelId(1L).raiderId(2L).build();

		var helix = mock(HystrixCommand.class);

		given(this.credential.getUserId()).willReturn("user");
		given(this.twitchHelix.sendShoutout(null, "1", "2", "user")).willReturn(helix);

		// When
		this.shoutoutRepository.sendShoutout(shoutout);

		// Then
		then(this.executor).should().execute(captor.capture());
		captor.getValue().run();
		then(this.credential).should().getUserId();
		then(this.twitchHelix).should().sendShoutout(null, "1", "2", "user");
		then(helix).should().execute();

		verifyNoMoreInteractions(this.twitchHelix, this.credential, this.executor);
	}

}
