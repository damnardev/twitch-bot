package fr.damnardev.twitch.bot.primary.adapter.event;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import fr.damnardev.twitch.bot.domain.model.event.LiveStatusEvent;
import fr.damnardev.twitch.bot.domain.port.primary.ILiveStatusService;
import fr.damnardev.twitch.bot.primary.adapter.AbstractChannelEventConsumer;
import org.springframework.stereotype.Service;

@Service
public class ChannelGoLiveEventConsumer extends AbstractChannelEventConsumer<ChannelGoLiveEvent, LiveStatusEvent> {

    public ChannelGoLiveEventConsumer(TwitchClient client, ILiveStatusService handler) {
        super(client, handler, ChannelGoLiveEvent.class);
    }

    @Override
    protected LiveStatusEvent toModel(ChannelGoLiveEvent event) {
        return LiveStatusEvent.builder()
                              .broadcasterId(event.getChannel()
                                                  .getId())
                              .broadcasterIdUserName(event.getChannel()
                                                          .getName())
                              .online(true)
                              .build();
    }

}
