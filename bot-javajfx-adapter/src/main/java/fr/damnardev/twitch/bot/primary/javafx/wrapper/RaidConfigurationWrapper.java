package fr.damnardev.twitch.bot.primary.javafx.wrapper;

import java.util.List;

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

	public RaidConfigurationWrapper(RaidConfiguration raidConfiguration) {
		this.messages = raidConfiguration.messages().stream().map(RaidConfigurationMessageWrapper::new).toList();
		this.id = new SimpleLongProperty(raidConfiguration.id());
		this.name = new SimpleStringProperty(raidConfiguration.name());
		this.twitchShoutoutEnabled = new SimpleBooleanProperty(raidConfiguration.twitchShoutoutEnabled());
		this.wizebotShoutoutEnabled = new SimpleBooleanProperty(raidConfiguration.wizebotShoutoutEnabled());
		this.raidMessageEnabled = new SimpleBooleanProperty(raidConfiguration.raidMessageEnabled());
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
