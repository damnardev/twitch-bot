package fr.damnardev.twitch.bot.domain.port.secondary.command;

import fr.damnardev.twitch.bot.domain.model.Command;

public interface UpdateChannelCommandRepository {

	void update(Command command);

}
