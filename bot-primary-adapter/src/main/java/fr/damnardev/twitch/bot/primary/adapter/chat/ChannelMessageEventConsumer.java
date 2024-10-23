package fr.damnardev.twitch.bot.primary.adapter.chat;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import fr.damnardev.twitch.bot.domain.model.User;
import fr.damnardev.twitch.bot.domain.model.event.MessageEvent;
import fr.damnardev.twitch.bot.domain.port.primary.event.IMessageEventService;
import fr.damnardev.twitch.bot.primary.adapter.AbstractChannelEventConsumer;
import org.springframework.stereotype.Service;

@Service
public class ChannelMessageEventConsumer extends AbstractChannelEventConsumer<ChannelMessageEvent, MessageEvent> {

    public ChannelMessageEventConsumer(TwitchClient twitchClient, IMessageEventService handler) {
        super(twitchClient, handler, ChannelMessageEvent.class);
    }

    @Override
    protected MessageEvent toModel(ChannelMessageEvent event) {
        var eventUser = event.getUser();
        var eventChannel = event.getChannel();
        var user = User.builder().name(eventUser.getName()).id(Long.parseLong(eventUser.getId())).build();
        var channel = User.builder().name(eventChannel.getName()).id(Long.parseLong(eventChannel.getId())).build();
        return MessageEvent.builder().user(user).channel(channel).message(event.getMessage()).build();
    }

}
