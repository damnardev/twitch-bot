package fr.damnardev.twitch.bot.port.secondary;

import fr.damnardev.twitch.bot.model.Message;

public interface MessageRepository {

	void sendMessage(Message message);

}
