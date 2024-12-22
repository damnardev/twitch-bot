package fr.damnardev.twitch.bot.secondary.adapter;

import java.util.concurrent.TimeUnit;

import com.github.twitch4j.chat.TwitchChat;
import fr.damnardev.twitch.bot.exception.FatalException;
import fr.damnardev.twitch.bot.model.Message;
import fr.damnardev.twitch.bot.port.secondary.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class DefaultMessageRepository implements MessageRepository {

	private final TwitchChat twitchChat;

	private final ThreadPoolTaskExecutor executor;

	@Override
	public void sendMessage(Message message) {
		log.info("Sending message {}", message);
		this.executor.execute(() -> {
			try {
				TimeUnit.SECONDS.sleep(2);
			}
			catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
				throw new FatalException(ex);
			}
			this.twitchChat.sendMessage(message.channelName(), message.content());
		});
	}

}
