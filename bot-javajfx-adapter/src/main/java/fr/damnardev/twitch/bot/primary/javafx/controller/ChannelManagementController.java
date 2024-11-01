package fr.damnardev.twitch.bot.primary.javafx.controller;

import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.model.form.CreateChannelForm;
import fr.damnardev.twitch.bot.domain.model.form.DeleteChannelForm;
import fr.damnardev.twitch.bot.domain.model.form.UpdateChannelEnabledForm;
import fr.damnardev.twitch.bot.domain.port.primary.CreateChannelService;
import fr.damnardev.twitch.bot.domain.port.primary.DeleteChannelService;
import fr.damnardev.twitch.bot.domain.port.primary.FindAllChannelService;
import fr.damnardev.twitch.bot.domain.port.primary.UpdateEnableChannelService;
import fr.damnardev.twitch.bot.primary.javafx.adapter.ApplicationStartupListener;
import fr.damnardev.twitch.bot.primary.javafx.wrapper.ChannelWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnBean(ApplicationStartupListener.class)
public class ChannelManagementController {

	private final StatusController statusController;

	private final CreateChannelService createChannelService;

	private final FindAllChannelService findAllChannelService;

	private final UpdateEnableChannelService updateEnableChannelService;

	private final DeleteChannelService deleteChannelService;

	@FXML
	public TableColumn<ChannelWrapper, String> columnDeleted;

	@FXML
	public TableColumn<ChannelWrapper, Boolean> columnEnabled;

	@FXML
	public TableColumn<ChannelWrapper, String> columnName;

	@FXML
	public TableColumn<ChannelWrapper, Number> columnId;

	@FXML
	public TableView<ChannelWrapper> tableView;

	@FXML
	private TextField textFieldChannelName;

	@FXML
	public void initialize() {
		this.columnId.setCellValueFactory((cell) -> cell.getValue().idProperty());
		this.columnName.setCellValueFactory((cell) -> cell.getValue().nameProperty());
		this.columnEnabled.setCellValueFactory((cell) -> cell.getValue().enabledProperty());
		this.columnEnabled.setCellFactory(CheckBoxTableCell.forTableColumn(this.columnEnabled));

		this.columnDeleted.setCellFactory(this::deletedCellFactory);

		reload();
	}

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
					this.button.setOnMouseClicked((event) -> {
						var form = DeleteChannelForm.builder().id(channel.idProperty().getValue()).name(channel.nameProperty().getValue()).build();
						ChannelManagementController.this.deleteChannelService.delete(form);
						getTableView().getItems().remove(channel);
					});
					this.button.setMaxWidth(Double.MAX_VALUE);
					setGraphic(this.button);
				}
			}
		};
	}


	private void reload() {
		var channels = this.findAllChannelService.findAll();
		var wrappers = channels.stream().map(this::buildWrapper).toList();
		this.tableView.getItems().clear();
		this.tableView.getItems().addAll(wrappers);
	}

	private ChannelWrapper buildWrapper(Channel channel) {
		var channelWrapper = new ChannelWrapper(channel);
		channelWrapper.enabledProperty().addListener((observable, oldValue, newValue) -> {
			var form = UpdateChannelEnabledForm.builder().id(channel.id()).name(channel.name()).enabled(newValue).build();
			this.updateEnableChannelService.updateEnabled(form);
		});
		return channelWrapper;
	}

	public void onButtonAdd() {
		var channelName = this.textFieldChannelName.getText();
		if (channelName.isBlank()) {
			this.statusController.setLabelText("Channel name is empty", true);
			return;
		}
		log.info("Try to add channel: {}", channelName);
		var form = CreateChannelForm.builder().name(channelName).build();
		try {
			var channel = this.createChannelService.save(form);
			this.statusController.setLabelText("Channel added: " + channelName, false);
			this.tableView.getItems().add(buildWrapper(channel));
			log.info("Channel added: {}", channel);
		}
		catch (Exception ex) {
			this.statusController.setLabelText("Error while adding channel: " + channelName, true);
			log.error("Error while adding channel: {}", channelName, ex);
		}
	}

}
