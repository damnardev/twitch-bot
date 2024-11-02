package fr.damnardev.twitch.bot.primary.javafx.listener;

import fr.damnardev.twitch.bot.domain.model.event.ChannelCreatedEvent;
import fr.damnardev.twitch.bot.domain.model.event.ChannelDeletedEvent;
import fr.damnardev.twitch.bot.domain.model.event.ChannelFindEvent;
import fr.damnardev.twitch.bot.domain.model.event.ChannelUpdatedEvent;
import fr.damnardev.twitch.bot.domain.model.event.ErrorEvent;
import fr.damnardev.twitch.bot.primary.javafx.adapter.ApplicationStartedEventListener;
import fr.damnardev.twitch.bot.primary.javafx.controller.ChannelManagementController;
import javafx.application.Platform;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnBean(ApplicationStartedEventListener.class)
public class JavaFXSpringEventListener {

	private final ChannelManagementController channelManagementController;

	@EventListener
	public void onChannelFindEvent(ChannelFindEvent event) {
		Platform.runLater(() -> this.channelManagementController.onChannelFindEvent(event));
	}

	@EventListener
	public void onChannelCreatedEvent(ChannelCreatedEvent event) {
		Platform.runLater(() -> this.channelManagementController.onChannelCreatedEvent(event));
	}

	@EventListener
	public void onChannelUpdatedEvent(ChannelUpdatedEvent event) {
		Platform.runLater(() -> this.channelManagementController.onChannelUpdatedEvent(event));
	}

	@EventListener
	public void onChannelDeletedEvent(ChannelDeletedEvent event) {
		Platform.runLater(() -> this.channelManagementController.onChannelDeletedEvent(event));
	}

	@EventListener
	public void onErrorEvent(ErrorEvent event) {
		Platform.runLater(() -> this.channelManagementController.onErrorEvent(event));
	}

}
