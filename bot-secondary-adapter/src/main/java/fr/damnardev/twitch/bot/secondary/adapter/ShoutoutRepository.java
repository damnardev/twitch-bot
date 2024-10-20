package fr.damnardev.twitch.bot.secondary.adapter;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import fr.damnardev.twitch.bot.domain.model.ChannelInfo;
import fr.damnardev.twitch.bot.domain.port.secondary.IShoutoutRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class ShoutoutRepository implements IShoutoutRepository {

    private final TwitchClient twitchClient;

    private final OAuth2Credential credential;

    @Override
    public void send(ChannelInfo from, ChannelInfo to) {
        twitchClient.getHelix()
                    .sendShoutout(null, from.id()
                                            .toString(), to.id()
                                                           .toString(), credential.getUserId())
                    .execute();
    }

}
