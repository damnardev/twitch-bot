package org.damnardev.twitch.bot.primary.adapter;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.damnardev.twitch.bot.domain.port.primary.IEventService;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractChannelEventConsumer<E, M> {

    private final TwitchClient client;

    private final IEventService<M> handler;

    private final Class<E> clazz;

    protected abstract M toModel(E event);

    @PostConstruct
    public void init() {
        var eventHandler = client.getEventManager()
                                 .getEventHandler(SimpleEventHandler.class);

        eventHandler.onEvent(clazz, this::process);
    }

    protected void process(E event) {
        var model = toModel(event);
        log.info("Event: {}", model);
        handler.process(model);
    }

}
