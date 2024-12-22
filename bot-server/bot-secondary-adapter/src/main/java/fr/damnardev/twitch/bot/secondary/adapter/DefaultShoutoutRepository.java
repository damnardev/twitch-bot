package fr.damnardev.twitch.bot.secondary.adapter;

import java.util.concurrent.TimeUnit;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.helix.TwitchHelix;
import fr.damnardev.twitch.bot.exception.FatalException;
import fr.damnardev.twitch.bot.model.Shoutout;
import fr.damnardev.twitch.bot.port.secondary.ShoutoutRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class DefaultShoutoutRepository implements ShoutoutRepository {

	private final TwitchHelix twitchHelix;

	private final OAuth2Credential credential;

	private final ThreadPoolTaskExecutor executor;

	@Override
	public void sendShoutout(Shoutout shoutout) {
		log.info("Sending shoutout {}", shoutout);
		this.executor.execute(() -> {
			try {
				TimeUnit.SECONDS.sleep(2);
			}
			catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
				throw new FatalException(ex);
			}
			this.twitchHelix.sendShoutout(null, shoutout.channelId().toString(), shoutout.raiderId().toString(), this.credential.getUserId()).execute();
		});
	}

}
