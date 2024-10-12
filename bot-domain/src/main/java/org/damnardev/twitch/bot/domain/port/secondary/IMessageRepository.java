package org.damnardev.twitch.bot.domain.port.secondary;

import org.damnardev.twitch.bot.domain.model.Message;

public interface IMessageRepository {

    void send(Message message);

}
