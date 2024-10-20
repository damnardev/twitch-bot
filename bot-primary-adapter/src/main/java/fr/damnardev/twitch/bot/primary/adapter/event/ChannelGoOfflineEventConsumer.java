package fr.damnardev.twitch.bot.primary.adapter.event;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import fr.damnardev.twitch.bot.domain.model.event.LiveStatusEvent;
import fr.damnardev.twitch.bot.domain.port.primary.ILiveStatusService;
import fr.damnardev.twitch.bot.primary.adapter.AbstractChannelEventConsumer;
import org.springframework.stereotype.Service;

@Service
public class ChannelGoOfflineEventConsumer extends AbstractChannelEventConsumer<ChannelGoOfflineEvent, LiveStatusEvent> {

    public ChannelGoOfflineEventConsumer(TwitchClient client, ILiveStatusService handler) {
        super(client, handler, ChannelGoOfflineEvent.class);
    }

    @Override
    protected LiveStatusEvent toModel(ChannelGoOfflineEvent event) {
        return LiveStatusEvent.builder()
                              .broadcasterId(event.getChannel()
                                                  .getId())
                              .broadcasterIdUserName(event.getChannel()
                                                          .getName())
                              .online(false)
                              .build();
    }

}
