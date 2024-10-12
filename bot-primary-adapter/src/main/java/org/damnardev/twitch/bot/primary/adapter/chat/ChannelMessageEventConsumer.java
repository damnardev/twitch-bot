package org.damnardev.twitch.bot.primary.adapter.chat;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.damnardev.twitch.bot.domain.model.event.MessageEvent;
import org.damnardev.twitch.bot.domain.port.primary.IMessageService;
import org.damnardev.twitch.bot.primary.adapter.AbstractChannelEventConsumer;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChannelMessageEventConsumer extends AbstractChannelEventConsumer<ChannelMessageEvent, MessageEvent> {

    public ChannelMessageEventConsumer(TwitchClient client, IMessageService handler) {
        super(client, handler, ChannelMessageEvent.class);
    }

    @Override
    protected MessageEvent toModel(ChannelMessageEvent event) {
        return MessageEvent.builder()
                           .fromUserName(event.getUser()
                                              .getName())
                           .toUserName(event.getChannel()
                                            .getName())
                           .message(event.getMessage())
                           .build();
    }

}
