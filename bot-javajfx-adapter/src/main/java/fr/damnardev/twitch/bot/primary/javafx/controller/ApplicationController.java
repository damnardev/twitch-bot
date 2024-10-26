package fr.damnardev.twitch.bot.primary.javafx.controller;

import fr.damnardev.twitch.bot.domain.model.ChannelInfo;
import fr.damnardev.twitch.bot.domain.model.User;
import fr.damnardev.twitch.bot.domain.port.primary.ICreateChanelService;
import fr.damnardev.twitch.bot.domain.port.primary.IDeleteChanelService;
import fr.damnardev.twitch.bot.domain.port.primary.IFindAllChanelService;
import fr.damnardev.twitch.bot.domain.port.primary.IUpdateChanelService;
import fr.damnardev.twitch.bot.primary.javafx.wrapper.ChannelInfoWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationController {

	private final ICreateChanelService createChanelService;

	private final IUpdateChanelService updateChanelService;

	private final IDeleteChanelService deleteChanelService;

	private final IFindAllChanelService findAllChanelService;

	@FXML
	private TableColumn<ChannelInfoWrapper, String> columnDeleted;

	@FXML
	private TableView<ChannelInfoWrapper> tableView;

	@FXML
	private TableColumn<ChannelInfoWrapper, Long> columnId;

	@FXML
	private TableColumn<ChannelInfoWrapper, String> columnName;

	@FXML
	private TableColumn<ChannelInfoWrapper, Boolean> columnEnabled;

	@FXML
	private TextField textFieldChannelName;

	@FXML
	private Label labelMessage;

	@FXML
	public void initialize() {
		this.tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
		this.columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		this.columnName.setCellValueFactory(new PropertyValueFactory<>("name"));

		this.columnEnabled.setCellValueFactory(this::enabledCellValueFactory);
		this.columnEnabled.setCellFactory(CheckBoxTableCell.forTableColumn(this.columnEnabled));

		this.columnDeleted.setCellFactory(this::deletedCellFactory);

		refreshItems();
	}

	private SimpleBooleanProperty enabledCellValueFactory(CellDataFeatures<ChannelInfoWrapper, Boolean> data) {
		var channel = data.getValue();
		var enabledProperty = new SimpleBooleanProperty(channel.getEnabled());

		enabledProperty.addListener((obs, wasEnabled, enabled) -> updateEnabledCellValue(enabled, channel));
		return enabledProperty;
	}

	private void updateEnabledCellValue(Boolean enabled, ChannelInfoWrapper channel) {
		channel.setEnabled(enabled);
		this.updateChanelService.update(channel.get());
	}

	private TableCell<ChannelInfoWrapper, String> deletedCellFactory(TableColumn<ChannelInfoWrapper, String> channelInfoWrapperStringTableColumn) {
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
						ApplicationController.this.deleteChanelService.delete(channel.get());
						getTableView().getItems().remove(channel);
					});
					this.button.setMaxWidth(Double.MAX_VALUE);
					setGraphic(this.button);
				}
			}
		};
	}

	private void refreshItems() {
		var channels = this.findAllChanelService.findAll().stream()
				.map(ChannelInfoWrapper::new).toList();
		this.tableView.getItems().clear();
		this.tableView.getItems().addAll(channels);
	}

	@FXML
	protected void onAddChannelClick() {
		var name = this.textFieldChannelName.getText();
		var user = User.builder().name(name).build();
		var channel = ChannelInfo.builder().user(user).build();
		log.info("Trying to add name {}", user);
		try {
			channel = this.createChanelService.create(channel);
			user = channel.user();
			var text = String.format("Channel name[%s], id[%s] added", user.name(), user.id());
			this.labelMessage.setText(text);
			refreshItems();
			log.info("Channel added {}", user);
		}
		catch (Exception ex) {
			var text = String.format("Error adding name %s with error %s", user, ex.getMessage());
			this.labelMessage.setText(text);
			log.error("Error adding name {}", user, ex);
		}
	}

}
