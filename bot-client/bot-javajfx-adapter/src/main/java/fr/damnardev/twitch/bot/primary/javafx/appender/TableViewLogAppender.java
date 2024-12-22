package fr.damnardev.twitch.bot.primary.javafx.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import fr.damnardev.twitch.bot.primary.javafx.wrapper.LoggerWrapper;
import javafx.collections.ObservableList;
import lombok.Setter;

@Setter
public class TableViewLogAppender extends AppenderBase<ILoggingEvent> {

	private ObservableList<LoggerWrapper> logMessages;

	@Override
	protected void append(ILoggingEvent eventObject) {
		this.logMessages.addFirst(new LoggerWrapper(eventObject));
		if (this.logMessages.size() > 300) {
			this.logMessages.removeLast();
		}
	}

}
