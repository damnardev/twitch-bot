package fr.damnardev.twitch.bot.secondary.adapter;

import fr.damnardev.twitch.bot.model.event.Event;
import fr.damnardev.twitch.bot.port.secondary.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class DefaultEventPublisher implements EventPublisher {

	private final ApplicationEventPublisher eventPublisher;

	@Override
	public <T extends Event> void publish(T event) {
		log.info("Publishing event {}", event.getClass().getSimpleName());
		this.eventPublisher.publishEvent(event);
		log.info("Published event {}", event.getClass().getSimpleName());
	}

}
