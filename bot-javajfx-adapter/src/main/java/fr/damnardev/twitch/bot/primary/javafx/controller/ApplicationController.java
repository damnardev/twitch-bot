package fr.damnardev.twitch.bot.primary.javafx.controller;

import fr.damnardev.twitch.bot.domain.model.ChannelInfo;
import fr.damnardev.twitch.bot.domain.model.User;
import fr.damnardev.twitch.bot.domain.port.primary.ICreateChanelService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationController {

    private final ICreateChanelService service;

    @FXML
    private TextField textFieldChannelName;

    @FXML
    private Label labelMessage;

    @FXML
    protected void onAddChannelClick() {
        var name = textFieldChannelName.getText();
        var user = User.builder().name(name).build();
        var channel = ChannelInfo.builder().user(user).build();
        log.info("Trying to add name {}", user);
        try {
            channel = service.create(channel);
            user = channel.user();
            var text = String.format("Channel name[%s], id[%s] added", user.name(), user.id());
            labelMessage.setText(text);
            log.info("Channel added {}", user);
        } catch (Exception e) {
            var text = String.format("Error adding name %s with error %s", user, e.getMessage());
            labelMessage.setText(text);
            log.error("Error adding name {}", user, e);
        }
    }

}
