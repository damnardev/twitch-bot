package fr.damnardev.twitch.bot.primary.javafx.wrapper;

import fr.damnardev.twitch.bot.domain.model.Channel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ChannelWrapper {

	private final LongProperty id;

	private final StringProperty name;

	private final BooleanProperty enabled;

	public ChannelWrapper(Channel channel) {
		this.id = new SimpleLongProperty(channel.id());
		this.name = new SimpleStringProperty(channel.name());
		this.enabled = new SimpleBooleanProperty(channel.enabled());
	}

	public LongProperty idProperty() {
		return this.id;
	}

	public StringProperty nameProperty() {
		return this.name;
	}

	public BooleanProperty enabledProperty() {
		return this.enabled;
	}

}
