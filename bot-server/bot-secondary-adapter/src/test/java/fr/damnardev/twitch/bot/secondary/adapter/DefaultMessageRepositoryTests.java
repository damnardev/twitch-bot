package fr.damnardev.twitch.bot.secondary.adapter;

import com.github.twitch4j.chat.TwitchChat;
import fr.damnardev.twitch.bot.model.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class DefaultMessageRepositoryTests {

	@InjectMocks
	private DefaultMessageRepository messageRepository;

	@Mock
	private TwitchChat twitchChat;

	@Mock
	private ThreadPoolTaskExecutor executor;

	@Test
	void sendMessage_shouldSendMessage_whenCalled() {
		// Given
		var captor = ArgumentCaptor.forClass(Runnable.class);
		var message = Message.builder().channelName("channel").content("content").build();

		// When
		this.messageRepository.sendMessage(message);

		// Then
		then(this.executor).should().execute(captor.capture());
		captor.getValue().run();
		then(this.twitchChat).should().sendMessage("channel", "content");
	}

}
