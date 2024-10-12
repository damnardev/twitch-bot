package org.damnardev.twitch.bot.primary.adapter.eventsub;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.eventsub.events.ChannelRaidEvent;
import org.damnardev.twitch.bot.domain.model.event.RaidEvent;
import org.damnardev.twitch.bot.domain.port.primary.IRaidEventService;
import org.damnardev.twitch.bot.primary.adapter.AbstractChannelEventConsumer;
import org.springframework.stereotype.Service;

@Service
public class ChannelRaidEventConsumer extends AbstractChannelEventConsumer<ChannelRaidEvent, RaidEvent> {

    public ChannelRaidEventConsumer(TwitchClient client, IRaidEventService handler) {
        super(client, handler, ChannelRaidEvent.class);
    }

    @Override
    protected RaidEvent toModel(ChannelRaidEvent event) {
        return RaidEvent.builder()
                        .fromUserName(event.getFromBroadcasterUserName())
                        .fromUserId(event.getFromBroadcasterUserId())
                        .toUserName(event.getToBroadcasterUserName())
                        .toUserId(event.getToBroadcasterUserId())
                        .build();
    }

}
