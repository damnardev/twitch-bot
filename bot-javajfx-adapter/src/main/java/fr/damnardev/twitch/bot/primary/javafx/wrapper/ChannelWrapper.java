package fr.damnardev.twitch.bot.primary.javafx.wrapper;

import fr.damnardev.twitch.bot.domain.model.Channel;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class ChannelWrapper {

	private final SimpleLongProperty id;

	private final SimpleStringProperty name;

	private final SimpleBooleanProperty enabled;

	private final SimpleBooleanProperty online;

	public ChannelWrapper(Channel channel) {
		this.id = new SimpleLongProperty(channel.id());
		this.name = new SimpleStringProperty(channel.name());
		this.enabled = new SimpleBooleanProperty(channel.enabled());
		this.online = new SimpleBooleanProperty(channel.online());
	}

	public SimpleLongProperty idProperty() {
		return this.id;
	}

	public SimpleStringProperty nameProperty() {
		return this.name;
	}

	public SimpleBooleanProperty enabledProperty() {
		return this.enabled;
	}

	public SimpleBooleanProperty onlineProperty() {
		return this.online;
	}

}
