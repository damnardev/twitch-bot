package fr.damnardev.twitch.bot.port.secondary.command;

import fr.damnardev.twitch.bot.model.Command;

public interface UpdateChannelCommandRepository {

	void update(Command command);

}
