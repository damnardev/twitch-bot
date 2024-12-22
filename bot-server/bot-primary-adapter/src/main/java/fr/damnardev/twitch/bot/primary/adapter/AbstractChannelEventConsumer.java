package fr.damnardev.twitch.bot.primary.adapter;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import fr.damnardev.twitch.bot.port.primary.EventService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractChannelEventConsumer<E, M> {

	private final ThreadPoolTaskExecutor executor;

	private final TwitchClient twitchClient;

	private final EventService<M> handler;

	private final Class<E> clazz;

	protected abstract M toModel(E event);

	@PostConstruct
	public void init() {
		var eventHandler = this.twitchClient.getEventManager().getEventHandler(SimpleEventHandler.class);
		eventHandler.onEvent(this.clazz, this::process);
	}

	protected void process(E event) {
		this.executor.execute(() -> doInternal(event));
	}

	private void doInternal(E event) {
		var model = toModel(event);
		log.info("Event: {}", model);
		this.handler.process(model);
	}

}
