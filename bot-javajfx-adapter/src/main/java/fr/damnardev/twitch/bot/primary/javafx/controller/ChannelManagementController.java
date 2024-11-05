package fr.damnardev.twitch.bot.primary.javafx.controller;

import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.model.event.ChannelCreatedEvent;
import fr.damnardev.twitch.bot.domain.model.event.ChannelDeletedEvent;
import fr.damnardev.twitch.bot.domain.model.event.ChannelFindEvent;
import fr.damnardev.twitch.bot.domain.model.event.ChannelUpdatedEvent;
import fr.damnardev.twitch.bot.domain.model.event.ErrorEvent;
import fr.damnardev.twitch.bot.domain.model.form.CreateChannelForm;
import fr.damnardev.twitch.bot.domain.model.form.DeleteChannelForm;
import fr.damnardev.twitch.bot.domain.model.form.UpdateChannelEnabledForm;
import fr.damnardev.twitch.bot.domain.port.primary.CreateChannelService;
import fr.damnardev.twitch.bot.domain.port.primary.DeleteChannelService;
import fr.damnardev.twitch.bot.domain.port.primary.FindAllChannelService;
import fr.damnardev.twitch.bot.domain.port.primary.UpdateChannelEnableService;
import fr.damnardev.twitch.bot.primary.javafx.adapter.ApplicationStartedEventListener;
import fr.damnardev.twitch.bot.primary.javafx.wrapper.ChannelWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
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

	public static final String ERROR_STR = "Error: %s";

	private final StatusController statusController;

	private final CreateChannelService createChannelService;

	private final FindAllChannelService findAllChannelService;

	private final UpdateChannelEnableService updateChannelEnableService;

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
		this.tableView.sort();
		this.tableView.setSelectionModel(null);
		this.tableView.setRowFactory((table) -> new TableRow<>() {
			@Override
			protected void updateItem(ChannelWrapper item, boolean empty) {
				super.updateItem(item, empty);
				if (item != null) {
					item.enabledProperty().addListener((observable, oldValue, newValue) -> this.updateRowColor(this.getItem()));
					item.onlineProperty().addListener((observable, oldValue, newValue) -> this.updateRowColor(this.getItem()));
				}
				updateRowColor(item);
			}

			private void updateRowColor(ChannelWrapper item) {
				if (item == null) {
					setStyle("");
				}
				else if (item.enabledProperty().get() && !item.onlineProperty().get()) {
					setStyle("-fx-background-color: lightcoral;");
				}
				else if (item.enabledProperty().get() && item.onlineProperty().get()) {
					setStyle("-fx-background-color: lightgreen;");
				}
				else {
					setStyle("");
				}
			}
		});
	}

	private void setupColumn() {
		this.columnId.setCellValueFactory((cell) -> cell.getValue().idProperty());
		this.columnName.setCellValueFactory((cell) -> cell.getValue().nameProperty());
		this.columnEnabled.setCellValueFactory((cell) -> cell.getValue().enabledProperty());
		this.columnEnabled.setCellFactory(CheckBoxTableCell.forTableColumn(this.columnEnabled));
		this.columnOnline.setCellValueFactory((cell) -> cell.getValue().onlineProperty());
		this.columnOnline.setCellFactory(CheckBoxTableCell.forTableColumn(this.columnEnabled));
		this.columnDeleted.setCellFactory(this::deletedCellFactory);
	}

	@SuppressWarnings("java:S110")
	private TableCell<ChannelWrapper, String> deletedCellFactory(TableColumn<ChannelWrapper, String> column) {
		return new TableCell<>() {

			final Button button = new Button("Delete");

			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setText(null);
					setGraphic(null);
				}
				else {
					var channel = getTableView().getItems().get(getIndex());
					this.button.setOnMouseClicked((event) -> ChannelManagementController.this.executor.execute(() -> {
						log.info("Try to delete channel: {}", channel);
						var form = DeleteChannelForm.builder().id(channel.idProperty().getValue()).name(channel.nameProperty().getValue()).build();
						ChannelManagementController.this.deleteChannelService.delete(form);
					}));
					this.button.setMaxWidth(Double.MAX_VALUE);
					setGraphic(this.button);
				}
			}
		};
	}

	private void refresh() {
		this.executor.execute(this.findAllChannelService::findAll);
	}

	public void onButtonAdd() {
		var channelName = this.textFieldChannelName.getText();
		if (channelName.isBlank()) {
			this.statusController.setLabelText("Channel name is empty", true);
			return;
		}
		log.info("Try to add channel: {}", channelName);
		var form = CreateChannelForm.builder().name(channelName).build();
		this.executor.execute(() -> this.createChannelService.save(form));
	}

	public void onChannelFindEvent(ChannelFindEvent event) {
		log.info("Channels found: {}", event.getChannels());
		var wrappers = event.getChannels().stream().map(this::buildWrapper).toList();
		this.tableView.getItems().clear();
		this.tableView.getItems().addAll(wrappers);
		this.tableView.sort();
	}

	private ChannelWrapper buildWrapper(Channel channel) {
		var channelWrapper = new ChannelWrapper(channel);
		channelWrapper.enabledProperty().addListener((observable, oldValue, newValue) -> {
			var form = UpdateChannelEnabledForm.builder().id(channel.id()).name(channel.name()).enabled(newValue).build();
			this.updateChannelEnableService.updateEnabled(form);
		});
		return channelWrapper;
	}

	public void onChannelCreatedEvent(ChannelCreatedEvent event) {
		log.info("Channel created: {}", event.getChannel());
		if (event.hasError()) {
			this.statusController.setLabelText(ERROR_STR.formatted(event.getError()), true);
			return;
		}
		this.tableView.getItems().add(buildWrapper(event.getChannel()));
		this.tableView.sort();
		this.statusController.setLabelText("Channel created: " + event.getChannel(), false);
	}

	public void onChannelUpdatedEvent(ChannelUpdatedEvent event) {
		log.info("Channel updated: {}", event.getChannel());
		if (event.hasError()) {
			this.statusController.setLabelText(ERROR_STR.formatted(event.getError()), true);
			return;
		}
		var channel = event.getChannel();
		var wrapper = this.tableView.getItems().stream().filter((w) -> w.idProperty().getValue().equals(channel.id())).findFirst().orElseThrow();
		wrapper.enabledProperty().set(channel.enabled());
		wrapper.onlineProperty().set(channel.online());
	}

	public void onChannelDeletedEvent(ChannelDeletedEvent event) {
		log.info("Channel deleted: {}", event.getChannel());
		if (event.hasError()) {
			this.statusController.setLabelText(ERROR_STR.formatted(event.getError()), true);
			return;
		}
		this.statusController.setLabelText("Channel deleted: " + event.getChannel(), false);
		refresh();
	}

	public void onErrorEvent(ErrorEvent event) {
		log.error("Error has occurred", event.getException());
		this.statusController.setLabelText(ERROR_STR.formatted(event.getException().getMessage()), true);
	}

}
