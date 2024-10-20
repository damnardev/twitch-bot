package fr.damnardev.twitch.bot.domain.port.secondary;

import fr.damnardev.twitch.bot.domain.model.Message;

public interface IMessageRepository {

    void send(Message message);

}
