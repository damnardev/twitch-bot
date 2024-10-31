package fr.damnardev.twitch.bot.primary.javafx.controller;

import fr.damnardev.twitch.bot.domain.model.form.CreateChannelForm;
import fr.damnardev.twitch.bot.domain.port.primary.ICreateChannelService;
import fr.damnardev.twitch.bot.domain.port.primary.IFindAllChannelService;
import fr.damnardev.twitch.bot.primary.javafx.adapter.ApplicationStartupListener;
import fr.damnardev.twitch.bot.primary.javafx.wrapper.ChannelWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
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

	private final ICreateChannelService createChannelService;

	private final IFindAllChannelService findAllChannelService;

	@FXML
	public TableColumn<ChannelWrapper, String> columnDeleted;

	@FXML
	public TableColumn<ChannelWrapper, Boolean> columnEnabled;

	@FXML
	public TableColumn<ChannelWrapper, String> columnName;

	@FXML
	public TableColumn<ChannelWrapper, Long> columnId;

	@FXML
	public TableView<ChannelWrapper> tableView;

	@FXML
	private TextField textFieldChannelName;

	@FXML
	public void initialize() {
		this.columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		this.columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		this.columnEnabled.setCellValueFactory(new PropertyValueFactory<>("enabled"));
		this.columnEnabled.setCellFactory(CheckBoxTableCell.forTableColumn(this.columnEnabled));

		var channels = this.findAllChannelService.findAll();
		var wrappers = channels.stream().map(ChannelWrapper::new).toList();
		this.tableView.getItems().addAll(wrappers);
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
			tableView.getItems().add(new ChannelWrapper(channel));
			log.info("Channel added: {}", channel);
		}
		catch (Exception ex) {
			this.statusController.setLabelText("Error while adding channel: " + channelName, true);
			log.error("Error while adding channel: {}", channelName, ex);
		}
	}

}
