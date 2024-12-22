package fr.damnardev.twitch.bot.primary.javafx.controller;

import fr.damnardev.twitch.bot.primary.javafx.adapter.ApplicationStartedEventListener;
import fr.damnardev.twitch.bot.primary.javafx.appender.TableViewLogAppender;
import fr.damnardev.twitch.bot.primary.javafx.wrapper.LoggerWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnBean(ApplicationStartedEventListener.class)
public class LogManagementController {

	@FXML
	public TableView<LoggerWrapper> tableView;

	@FXML
	public TableColumn<LoggerWrapper, String> columnTime;

	@FXML
	public TableColumn<LoggerWrapper, String> columnThread;

	@FXML
	public TableColumn<LoggerWrapper, String> columnLevel;

	@FXML
	public TableColumn<LoggerWrapper, String> columnLogger;

	@FXML
	public TableColumn<LoggerWrapper, String> columnMessage;

	@FXML
	public Button buttonAction;

	@FXML
	public TextField textFieldFilter;

	private ObservableList<LoggerWrapper> logMessages;

	private TableViewLogAppender appender;

	private FilteredList<LoggerWrapper> filteredData;

	@FXML
	public void initialize() {
		setupTableView();
		setupColumn();
		refresh();
	}

	private void setupTableView() {
		this.logMessages = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
		this.filteredData = new FilteredList<>(this.logMessages, (x) -> true);
		this.tableView.setItems(this.filteredData);
	}

	private void setupColumn() {
		this.columnTime.setCellValueFactory((cellData) -> cellData.getValue().timeProperty());
		this.columnThread.setCellValueFactory((cellData) -> cellData.getValue().threadProperty());
		this.columnLevel.setCellValueFactory((cellData) -> cellData.getValue().levelProperty());
		this.columnLogger.setCellValueFactory((cellData) -> cellData.getValue().loggerProperty());
		this.columnMessage.setCellValueFactory((cellData) -> cellData.getValue().messageProperty());
	}

	private void refresh() {
		this.appender = new TableViewLogAppender();
		this.appender.setLogMessages(this.logMessages);
		var rootLogger = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		rootLogger.addAppender(this.appender);
		this.appender.setContext((ch.qos.logback.classic.LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory());
		this.appender.start();

		this.buttonAction.setText("Stop");
	}

	public void onKeyPressed() {
		this.filteredData.setPredicate((entry) -> {
			var filter = this.textFieldFilter.getText();
			if (filter == null || filter.isEmpty()) {
				return true;
			}
			return entry.messageProperty().getValue().contains(filter);
		});
	}

	public void onButtonAction() {
		if (this.appender.isStarted()) {
			this.appender.stop();
			this.buttonAction.setText("Start");
		}
		else {
			this.appender.start();
			this.buttonAction.setText("Stop");
		}
	}

	public void onButtonClear() {
		this.logMessages.clear();
	}

}
