package fr.damnardev.twitch.bot.primary.javafx.wrapper;

import java.util.List;
import java.util.stream.Collectors;

import fr.damnardev.twitch.bot.domain.model.RaidConfiguration;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class RaidConfigurationWrapper {

	private final List<RaidConfigurationMessageWrapper> messages;

	private final SimpleLongProperty id;

	private final SimpleStringProperty name;

	private final SimpleBooleanProperty twitchShoutoutEnabled;

	private final SimpleBooleanProperty wizebotShoutoutEnabled;

	private final SimpleBooleanProperty raidMessageEnabled;

	@SuppressWarnings("java:S6204")
	public RaidConfigurationWrapper(RaidConfiguration configuration) {
		this.messages = configuration.messages().stream().map((message) -> new RaidConfigurationMessageWrapper(configuration.channelId(), configuration.channelName(), message)).collect(Collectors.toList());
		this.id = new SimpleLongProperty(configuration.channelId());
		this.name = new SimpleStringProperty(configuration.channelName());
		this.twitchShoutoutEnabled = new SimpleBooleanProperty(configuration.twitchShoutoutEnabled());
		this.wizebotShoutoutEnabled = new SimpleBooleanProperty(configuration.wizebotShoutoutEnabled());
		this.raidMessageEnabled = new SimpleBooleanProperty(configuration.raidMessageEnabled());
	}

	public SimpleLongProperty idProperty() {
		return this.id;
	}

	public SimpleStringProperty nameProperty() {
		return this.name;
	}

	public SimpleBooleanProperty twitchShoutoutEnabledProperty() {
		return this.twitchShoutoutEnabled;
	}

	public SimpleBooleanProperty wizebotShoutoutEnabledProperty() {
		return this.wizebotShoutoutEnabled;
	}

	public SimpleBooleanProperty raidMessageEnabledProperty() {
		return this.raidMessageEnabled;
	}

	public List<RaidConfigurationMessageWrapper> getMessages() {
		return this.messages;
	}

}
