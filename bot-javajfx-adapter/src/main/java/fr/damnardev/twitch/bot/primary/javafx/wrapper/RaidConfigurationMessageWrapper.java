package fr.damnardev.twitch.bot.primary.javafx.wrapper;

import javafx.beans.property.SimpleStringProperty;

public class RaidConfigurationMessageWrapper {

	private final SimpleStringProperty message;

	public RaidConfigurationMessageWrapper(String message) {
		this.message = new SimpleStringProperty(message);
	}

	public SimpleStringProperty messageProperty() {
		return this.message;
	}

}
