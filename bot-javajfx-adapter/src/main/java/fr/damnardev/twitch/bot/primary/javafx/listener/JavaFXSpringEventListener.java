package fr.damnardev.twitch.bot.primary.javafx.listener;

import fr.damnardev.twitch.bot.domain.model.event.ChannelCreatedEvent;
import fr.damnardev.twitch.bot.domain.model.event.ChannelDeletedEvent;
import fr.damnardev.twitch.bot.domain.model.event.ChannelFetchedAllEvent;
import fr.damnardev.twitch.bot.domain.model.event.ChannelUpdatedEvent;
import fr.damnardev.twitch.bot.domain.model.event.ErrorEvent;
import fr.damnardev.twitch.bot.domain.model.event.RaidConfigurationFetchedAllEvent;
import fr.damnardev.twitch.bot.domain.model.event.RaidConfigurationFetchedEvent;
import fr.damnardev.twitch.bot.domain.model.event.RaidConfigurationUpdatedEvent;
import fr.damnardev.twitch.bot.primary.javafx.adapter.ApplicationStartedEventListener;
import fr.damnardev.twitch.bot.primary.javafx.controller.ChannelManagementController;
import fr.damnardev.twitch.bot.primary.javafx.controller.RaidConfigurationController;
import fr.damnardev.twitch.bot.primary.javafx.controller.StatusController;
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

	private final RaidConfigurationController raidConfigurationController;

	private final StatusController statusController;

	@EventListener
	public void onChannelFindEvent(ChannelFetchedAllEvent event) {
		Platform.runLater(() -> this.channelManagementController.onChannelFindEvent(event));
	}

	@EventListener
	public void onChannelCreatedEvent(ChannelCreatedEvent event) {
		Platform.runLater(() -> this.channelManagementController.onChannelCreatedEvent(event));
		Platform.runLater(() -> this.raidConfigurationController.onChannelCreatedEvent(event));
	}

	@EventListener
	public void onChannelUpdatedEvent(ChannelUpdatedEvent event) {
		Platform.runLater(() -> this.channelManagementController.onChannelUpdatedEvent(event));
	}

	@EventListener
	public void onChannelDeletedEvent(ChannelDeletedEvent event) {
		Platform.runLater(() -> this.channelManagementController.onChannelDeletedEvent(event));
		Platform.runLater(() -> this.raidConfigurationController.onChannelDeletedEvent(event));
	}

	@EventListener
	public void onRaidConfigurationFindAllEvent(RaidConfigurationFetchedAllEvent event) {
		Platform.runLater(() -> this.raidConfigurationController.onRaidConfigurationFindAllEvent(event));
	}

	@EventListener
	public void onRaidConfigurationFindEvent(RaidConfigurationFetchedEvent event) {
		Platform.runLater(() -> this.raidConfigurationController.onRaidConfigurationFindEvent(event));
	}

	@EventListener
	public void onRaidConfigurationUpdatedEvent(RaidConfigurationUpdatedEvent event) {
		Platform.runLater(() -> this.raidConfigurationController.onRaidConfigurationUpdatedEvent(event));
	}

	@EventListener
	public void onErrorEvent(ErrorEvent event) {
		Platform.runLater(() -> this.statusController.onErrorEvent(event));
	}

}
