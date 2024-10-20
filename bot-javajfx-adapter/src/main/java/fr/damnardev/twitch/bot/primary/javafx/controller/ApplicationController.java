package fr.damnardev.twitch.bot.primary.javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import fr.damnardev.twitch.bot.domain.model.ChannelInfo;
import fr.damnardev.twitch.bot.domain.port.primary.IAddChanelService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationController {

    @FXML
    private TextField textFieldChannelName;

    @FXML
    private Label labelMessage;

    private final IAddChanelService service;

    @FXML
    protected void onAddChannelClick() {
        var channel = textFieldChannelName.getText();
        var channelInfo = ChannelInfo.builder()
                                     .name(channel)
                                     .build();
        log.info("Trying to add channel {}", channelInfo);
        try {
            channelInfo = service.process(channelInfo);
            var text = String.format("Channel name[%s], id[%s] added", channelInfo.name(), channelInfo.id());
            labelMessage.setText(text);
            log.info("Channel added {}", channelInfo);
        } catch (Exception e) {
            var text = String.format("Error adding channel %s with error %s", channelInfo, e.getMessage());
            labelMessage.setText(text);
            log.error("Error adding channel {}", channelInfo, e);
        }
    }

}