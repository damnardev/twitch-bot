package fr.damnardev.twitch.bot.secondary.adapter;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import fr.damnardev.twitch.bot.domain.model.User;
import fr.damnardev.twitch.bot.domain.port.secondary.IShoutoutRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class ShoutoutRepository implements IShoutoutRepository {

    private final TwitchClient twitchClient;

    private final OAuth2Credential credential;

    @Override
    @Transactional
    public void send(User channel, User raider) {
        log.info("Sending shoutout to {} for {}", channel, raider);
        twitchClient.getHelix().sendShoutout(null, channel.id().toString(), raider.id().toString(), credential.getUserId()).execute();
    }

}
