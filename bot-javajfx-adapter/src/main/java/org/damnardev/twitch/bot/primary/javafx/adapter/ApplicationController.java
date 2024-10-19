package org.damnardev.twitch.bot.primary.javafx.adapter;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.damnardev.twitch.bot.domain.model.ChannelInfo;
import org.damnardev.twitch.bot.domain.port.primary.IAddChanelService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationController {

    @FXML
    private TextField channelName;

    @FXML
    private Label message;

    private final IAddChanelService service;

    @FXML
    protected void onAddChannelClick() {
        var channel = channelName.getText();
        var channelInfo = ChannelInfo.builder()
                                     .name(channel)
                                     .build();
        log.info("Trying to add channel {}", channelInfo);
        try {
            channelInfo = service.process(channelInfo);
            var text = String.format("Channel name[%s], id[%s] added", channelInfo.name(), channelInfo.id());
            message.setText(text);
            log.info("Channel added {}", channelInfo);
        } catch (Exception e) {
            var text = String.format("Error adding channel %s with error %s", channelInfo, e.getMessage());
            message.setText(text);
            log.error("Error adding channel {}", channelInfo, e);
        }
    }

}