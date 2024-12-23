package fr.damnardev.twitch.bot.primary.javafx.controller;

import fr.damnardev.twitch.bot.model.Channel;
import fr.damnardev.twitch.bot.model.event.ChannelCreatedEvent;
import fr.damnardev.twitch.bot.model.event.ChannelDeletedEvent;
import fr.damnardev.twitch.bot.model.event.ChannelFetchedAllEvent;
import fr.damnardev.twitch.bot.model.event.ChannelUpdatedEvent;
import fr.damnardev.twitch.bot.model.form.CreateChannelForm;
import fr.damnardev.twitch.bot.model.form.DeleteChannelForm;
import fr.damnardev.twitch.bot.model.form.UpdateChannelForm;
import fr.damnardev.twitch.bot.port.primary.channel.CreateChannelService;
import fr.damnardev.twitch.bot.port.primary.channel.DeleteChannelService;
import fr.damnardev.twitch.bot.port.primary.channel.FetchAllChannelService;
import fr.damnardev.twitch.bot.port.primary.channel.UpdateChannelService;
import fr.damnardev.twitch.bot.primary.javafx.adapter.ApplicationStartedEventListener;
import fr.damnardev.twitch.bot.primary.javafx.control.ChannelWrapperTableRow;
import fr.damnardev.twitch.bot.primary.javafx.control.UnfocusableButtonTableCell;
import fr.damnardev.twitch.bot.primary.javafx.control.UnfocusableCheckBoxTableCell;
import fr.damnardev.twitch.bot.primary.javafx.wrapper.ChannelWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnBean(ApplicationStartedEventListener.class)
public class ChannelManagementController {

	private final StatusController statusController;

	private final CreateChannelService createChannelService;

	private final FetchAllChannelService fetchAllChannelService;

	private final UpdateChannelService updateChannelService;

	private final DeleteChannelService deleteChannelService;

	private final ThreadPoolTaskExecutor executor;

	@FXML
	public TableColumn<ChannelWrapper, String> columnName;

	@FXML
	public TableColumn<ChannelWrapper, Number> columnId;

	@FXML
	public TableColumn<ChannelWrapper, Boolean> columnEnabled;

	@FXML
	public TableColumn<ChannelWrapper, Boolean> columnOnline;

	@FXML
	public TableColumn<ChannelWrapper, String> columnDeleted;

	@FXML
	public TableView<ChannelWrapper> tableView;

	@FXML
	private TextField textFieldChannelName;

	@FXML
	public void initialize() {
		setupTableView();
		setupColumn();
		refresh();
	}

	private void setupTableView() {
		this.tableView.getSortOrder().add(this.columnId);
		this.tableView.setRowFactory((x) -> new ChannelWrapperTableRow());
	}

	private void setupColumn() {
		this.columnId.setCellValueFactory((cell) -> cell.getValue().idProperty());
		this.columnName.setCellValueFactory((cell) -> cell.getValue().nameProperty());
		this.columnEnabled.setCellValueFactory((cell) -> cell.getValue().enabledProperty());
		this.columnEnabled.setCellFactory((x) -> new UnfocusableCheckBoxTableCell<>());
		this.columnOnline.setCellValueFactory((cell) -> cell.getValue().onlineProperty());
		this.columnOnline.setCellFactory((x) -> new UnfocusableCheckBoxTableCell<>());
		this.columnDeleted.setCellFactory((x) -> new UnfocusableButtonTableCell<>(this::onButtonDelete));
	}

	private void refresh() {
		this.executor.execute(this.fetchAllChannelService::process);
	}

	private void sort() {
		this.tableView.sort();
		this.tableView.refresh();
	}

	private ChannelWrapper buildWrapper(Channel channel) {
		var channelWrapper = new ChannelWrapper(channel);
		channelWrapper.enabledProperty().addListener((observable, oldValue, newValue) -> {
			var form = UpdateChannelForm.builder().id(channel.id()).name(channel.name()).enabled(newValue).build();
			this.updateChannelService.process(form);
		});
		return channelWrapper;
	}

	public void onEnterKeyPressed() {
		this.onButtonAdd();
	}

	public void onKeyPressed(KeyEvent keyEvent) {
		if (keyEvent.getCode().equals(KeyCode.DELETE)) {
			var selectedItem = this.tableView.getSelectionModel().getSelectedItem();
			if (selectedItem != null) {
				log.info("Try to delete channel: {}", selectedItem);
				var form = DeleteChannelForm.builder().id(selectedItem.idProperty().getValue()).name(selectedItem.nameProperty().getValue()).build();
				this.executor.execute(() -> this.deleteChannelService.process(form));
			}
		}
		else if (keyEvent.getCode().equals(KeyCode.E)) {
			var selectedItem = this.tableView.getSelectionModel().getSelectedItem();
			if (selectedItem != null) {
				selectedItem.enabledProperty().set(!selectedItem.enabledProperty().get());
			}
		}
	}

	public void onButtonAdd() {
		var channelName = this.textFieldChannelName.getText();
		if (channelName.isBlank()) {
			this.statusController.setLabelText("Channel channelName is empty", true);
			return;
		}
		log.info("Try to add channel: {}", channelName);
		var form = CreateChannelForm.builder().name(channelName).build();
		this.executor.execute(() -> this.createChannelService.process(form));
	}

	private void onButtonDelete(ChannelWrapper channel) {
		log.info("Try to delete channel: {}", channel);
		var form = DeleteChannelForm.builder().id(channel.idProperty().getValue()).name(channel.nameProperty().getValue()).build();
		this.executor.execute(() -> ChannelManagementController.this.deleteChannelService.process(form));
	}

	public void onChannelFindEvent(ChannelFetchedAllEvent event) {
		log.info("Channels found [size]: {}", event.getValue().size());
		var wrappers = event.getValue().stream().map(this::buildWrapper).toList();
		this.tableView.getItems().clear();
		this.tableView.getItems().addAll(wrappers);
		sort();
	}

	public void onChannelCreatedEvent(ChannelCreatedEvent event) {
		log.info("Channel created: {}", event.getValue());
		this.tableView.getItems().add(buildWrapper(event.getValue()));
		sort();
		this.statusController.setLabelText("Channel created: " + event.getValue(), false);
	}

	public void onChannelUpdatedEvent(ChannelUpdatedEvent event) {
		log.info("Channel updated: {}", event.getValue());
		var channel = event.getValue();
		var wrapper = this.tableView.getItems().stream().filter((item) -> item.idProperty().getValue().equals(channel.id())).findFirst().orElseThrow();
		wrapper.enabledProperty().set(channel.enabled());
		wrapper.onlineProperty().set(channel.online());
		this.statusController.setLabelText("Channel updated: " + event.getValue(), false);
	}

	public void onChannelDeletedEvent(ChannelDeletedEvent event) {
		log.info("Channel deleted: {}", event.getValue());
		this.tableView.getItems().removeIf((item) -> item.idProperty().getValue().equals(event.getValue().id()));
		sort();
		this.statusController.setLabelText("Channel deleted: " + event.getValue(), false);
	}

}
