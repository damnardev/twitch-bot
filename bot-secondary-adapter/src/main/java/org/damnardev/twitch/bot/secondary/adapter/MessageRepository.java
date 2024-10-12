package org.damnardev.twitch.bot.secondary.adapter;

import com.github.twitch4j.TwitchClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.damnardev.twitch.bot.domain.model.Message;
import org.damnardev.twitch.bot.domain.port.secondary.IMessageRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class MessageRepository implements IMessageRepository {

    private final TwitchClient twitchClient;

    @Override
    public void send(Message message) {
        twitchClient.getChat()
                    .sendMessage(message.channel()
                                        .name(), message.value());
    }

}
