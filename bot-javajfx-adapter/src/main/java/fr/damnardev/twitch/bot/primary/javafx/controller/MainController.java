package fr.damnardev.twitch.bot.primary.javafx.controller;

import fr.damnardev.twitch.bot.domain.model.form.CreateChannelForm;
import fr.damnardev.twitch.bot.domain.port.primary.ICreateChannelService;
import fr.damnardev.twitch.bot.primary.javafx.adapter.ApplicationStartupListener;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnBean(ApplicationStartupListener.class)
public class MainController {

	private final StatusController statusController;

	private final ICreateChannelService createChannelService;

	@FXML
	private TextField textFieldChannelName;

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
			log.info("Channel added: {}", channel);
		}
		catch (Exception ex) {
			this.statusController.setLabelText("Error while adding channel: " + channelName, true);
			log.error("Error while adding channel: {}", channelName, ex);
		}
	}

}
