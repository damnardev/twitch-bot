package fr.damnardev.twitch.bot.primary.adapter.eventsub;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.eventsub.socket.events.EventSocketClosedByTwitchEvent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import fr.damnardev.twitch.bot.domain.port.primary.IAuthenticationService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class EventSocketClosedByTwitchEventConsumer {

    private final TwitchClient client;

    private final IAuthenticationService handler;

    @PostConstruct
    public void init() {
        var eventHandler = client.getEventSocket()
                                 .getEventManager()
                                 .getEventHandler(SimpleEventHandler.class);

        eventHandler.onEvent(EventSocketClosedByTwitchEvent.class, this::process);
    }

    private void process(EventSocketClosedByTwitchEvent event) {
        log.info("Event socket has been closed by Twitch");
        handler.reconnect();
    }

}
