package fr.damnardev.twitch.bot.primary.javafx.wrapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

public class LoggerWrapper {

	private final SimpleStringProperty time;

	private final SimpleStringProperty thread;

	private final SimpleStringProperty level;

	private final SimpleStringProperty logger;

	private final SimpleStringProperty message;

	public LoggerWrapper(ILoggingEvent eventObject) {
		var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS");
		var localDateTime = LocalDateTime.ofInstant(eventObject.getInstant(), ZoneId.systemDefault());
		this.time = new SimpleStringProperty(formatter.format(localDateTime));
		this.thread = new SimpleStringProperty(eventObject.getThreadName());
		this.level = new SimpleStringProperty(eventObject.getLevel().toString());
		this.logger = new SimpleStringProperty(eventObject.getLoggerName());
		this.message = new SimpleStringProperty(eventObject.getFormattedMessage());
	}

	public SimpleStringProperty timeProperty() {
		return this.time;
	}

	public ObservableValue<String> threadProperty() {
		return this.thread;
	}

	public ObservableValue<String> levelProperty() {
		return this.level;
	}

	public ObservableValue<String> loggerProperty() {
		return this.logger;
	}

	public SimpleStringProperty messageProperty() {
		return this.message;
	}

}
