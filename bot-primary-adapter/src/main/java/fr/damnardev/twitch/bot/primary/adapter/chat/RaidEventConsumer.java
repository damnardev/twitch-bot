package fr.damnardev.twitch.bot.primary.adapter.chat;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.RaidEvent;
import fr.damnardev.twitch.bot.domain.model.User;
import fr.damnardev.twitch.bot.domain.port.primary.event.IRaidEventService;
import fr.damnardev.twitch.bot.primary.adapter.AbstractChannelEventConsumer;
import org.springframework.stereotype.Service;

@Service
public class RaidEventConsumer extends AbstractChannelEventConsumer<RaidEvent, fr.damnardev.twitch.bot.domain.model.event.RaidEvent> {

    public RaidEventConsumer(TwitchClient twitchClient, IRaidEventService handler) {
        super(twitchClient, handler, RaidEvent.class);
    }

    @Override
    protected fr.damnardev.twitch.bot.domain.model.event.RaidEvent toModel(RaidEvent event) {
        var eventRaider = event.getRaider();
        var eventChannel = event.getChannel();
        var raider = User.builder().name(eventRaider.getName()).id(Long.parseLong(eventRaider.getId())).build();
        var channel = User.builder().name(eventChannel.getName()).id(Long.parseLong(eventChannel.getId())).build();
        return fr.damnardev.twitch.bot.domain.model.event.RaidEvent.builder().raider(raider).channel(channel).build();
    }

}
