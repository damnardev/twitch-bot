package fr.damnardev.twitch.bot.primary.javafx.controller;

import fr.damnardev.twitch.bot.domain.model.RaidConfiguration;
import fr.damnardev.twitch.bot.domain.model.event.RaidConfigurationFindEvent;
import fr.damnardev.twitch.bot.domain.model.event.RaidConfigurationUpdatedEvent;
import fr.damnardev.twitch.bot.domain.model.form.CreateRaidConfigurationMessageForm;
import fr.damnardev.twitch.bot.domain.port.primary.CreateRaidConfigurationMessageService;
import fr.damnardev.twitch.bot.domain.port.primary.FindAllRaidConfigurationService;
import fr.damnardev.twitch.bot.primary.javafx.adapter.ApplicationStartedEventListener;
import fr.damnardev.twitch.bot.primary.javafx.wrapper.RaidConfigurationMessageWrapper;
import fr.damnardev.twitch.bot.primary.javafx.wrapper.RaidConfigurationWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
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
public class RaidConfigurationController {

	public static final String ERROR_STR = "Error: %s";

	private final FindAllRaidConfigurationService findAllRaidConfigurationService;

	private final StatusController statusController;

	private final ThreadPoolTaskExecutor executor;

	private final CreateRaidConfigurationMessageService createRaidConfigurationMessageService;

	@FXML
	public TableView<RaidConfigurationWrapper> tableViewRaidConfiguration;

	@FXML
	public TableColumn<RaidConfigurationWrapper, Number> columnId;

	@FXML
	public TableColumn<RaidConfigurationWrapper, String> columnName;

	@FXML
	public TableColumn<RaidConfigurationWrapper, Boolean> columnTwitchShoutoutEnabled;

	@FXML
	public TableColumn<RaidConfigurationWrapper, Boolean> columnWizebotShoutoutEnabled;

	@FXML
	public TableColumn<RaidConfigurationWrapper, Boolean> columnRaidMessageEnabled;

	@FXML
	public TableColumn<RaidConfigurationMessageWrapper, String> columnMessage;

	@FXML
	public TableView<RaidConfigurationMessageWrapper> tableViewMessage;

	@FXML
	public TextField textFieldMessage;

	@FXML
	public void initialize() {
		setupTableView();
		setupColumn();
		refresh();
	}

	private void setupTableView() {
		this.tableViewRaidConfiguration.getSortOrder().add(this.columnId);
		this.tableViewRaidConfiguration.sort();
		this.tableViewRaidConfiguration.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				this.tableViewMessage.getItems().clear();
				this.tableViewMessage.getItems().addAll(newValue.getMessages());
			}
		});
	}

	private void setupColumn() {
		this.columnId.setCellValueFactory((cell) -> cell.getValue().idProperty());
		this.columnName.setCellValueFactory((cell) -> cell.getValue().nameProperty());
		this.columnTwitchShoutoutEnabled.setCellValueFactory((cell) -> cell.getValue().twitchShoutoutEnabledProperty());
		this.columnTwitchShoutoutEnabled.setCellFactory(CheckBoxTableCell.forTableColumn(this.columnTwitchShoutoutEnabled));
		this.columnWizebotShoutoutEnabled.setCellValueFactory((cell) -> cell.getValue().wizebotShoutoutEnabledProperty());
		this.columnWizebotShoutoutEnabled.setCellFactory(CheckBoxTableCell.forTableColumn(this.columnWizebotShoutoutEnabled));
		this.columnRaidMessageEnabled.setCellValueFactory((cell) -> cell.getValue().raidMessageEnabledProperty());
		this.columnRaidMessageEnabled.setCellFactory(CheckBoxTableCell.forTableColumn(this.columnRaidMessageEnabled));
		this.columnMessage.setCellValueFactory((cell) -> cell.getValue().messageProperty());
	}

	private void refresh() {
		this.executor.execute(this.findAllRaidConfigurationService::findAll);
	}

	public void onRaidConfigurationFindEvent(RaidConfigurationFindEvent event) {
		log.info("Configurations found: {}", event);
		var wrappers = event.getConfigurations().stream().map(this::buildWrapper).toList();
		this.tableViewRaidConfiguration.getItems().clear();
		this.tableViewRaidConfiguration.getItems().addAll(wrappers);
		this.tableViewRaidConfiguration.sort();
	}

	private RaidConfigurationWrapper buildWrapper(RaidConfiguration configuration) {
		return new RaidConfigurationWrapper(configuration);
	}

	public void onButtonAdd() {
		var message = this.textFieldMessage.getText();
		if (message.isBlank()) {
			this.statusController.setLabelText("Channel name is empty", true);
			return;
		}
		log.info("Try to add message: {}", message);
		var raidConfiguration = this.tableViewRaidConfiguration.getFocusModel().getFocusedItem();
		var channelId = raidConfiguration.idProperty().get();
		var channelName = raidConfiguration.nameProperty().get();
		var form = CreateRaidConfigurationMessageForm.builder().id(channelId).name(channelName).message(message).build();
		this.executor.execute(() -> this.createRaidConfigurationMessageService.save(form));
	}

	public void onRaidConfigurationUpdatedEvent(RaidConfigurationUpdatedEvent event) {
		log.info("Configuration updated: {}", event);
		if (event.hasError()) {
			this.statusController.setLabelText(ERROR_STR.formatted(event.getError()), true);
			return;
		}
		var raidConfiguration = getRaidConfiguration(event);
		updateRaidConfigurationMessage(event, raidConfiguration);
	}

	private RaidConfigurationWrapper getRaidConfiguration(RaidConfigurationUpdatedEvent event) {
		return this.tableViewRaidConfiguration.getItems().stream().filter((wrapper) -> wrapper.idProperty().get() == event.getRaidConfiguration().id()).findFirst().orElseThrow();
	}

	private void updateRaidConfigurationMessage(RaidConfigurationUpdatedEvent event, RaidConfigurationWrapper raidConfiguration) {
		raidConfiguration.getMessages().clear();
		event.getRaidConfiguration().messages().forEach((message) -> raidConfiguration.getMessages().add(new RaidConfigurationMessageWrapper(message)));
		this.tableViewMessage.getItems().clear();
		this.tableViewMessage.getItems().addAll(raidConfiguration.getMessages());
		this.statusController.setLabelText("Message added for the users " + event.getRaidConfiguration().name(), false);
	}

}
